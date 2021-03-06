package ro.polak.http.servlet;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import ro.polak.http.ServerConfig;
import ro.polak.http.session.storage.SessionStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServletContextWrapperTest {

    private SessionStorage sessionStorage;
    private ServletContextWrapper servletContext;

    @Before
    public void setup() {
        ServerConfig serverConfig = mock(ServerConfig.class);
        sessionStorage = mock(SessionStorage.class);
        servletContext = new ServletContextWrapper(serverConfig, sessionStorage);
    }

    @Test
    public void shouldSetCookieAndPersistForValidSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        HttpResponseWrapper response = new HttpResponseWrapper();
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).persistSession(session);

        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionWrapper.COOKIE_NAME)) {
                assertThat(cookie.getValue(), is(not(nullValue())));
                return;
            }
        }

        fail("Session cookie was not set.");
    }

    @Test
    public void shouldEraseCookieAndRemoveForInvalidatedSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        HttpResponseWrapper response = new HttpResponseWrapper();
        session.invalidate();
        servletContext.handleSession(session, response);
        verify(sessionStorage, times(1)).removeSession(session);

        for (Cookie cookie : response.getCookies()) {
            if (cookie.getName().equals(HttpSessionWrapper.COOKIE_NAME)) {
                assertThat(cookie.getMaxAge(), lessThan(-1));
                return;
            }
        }

        fail("Session DELETE cookie was not set.");
    }

    @Test
    public void shouldReturnSessionForValidSID() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionWrapper sessionRead = servletContext.getSession("123");
        assertThat(sessionRead, is(not(nullValue())));
        assertThat(sessionRead.getServletContext(), is((ServletContext) servletContext));
    }

    @Test
    public void shouldRemoveExpiredSession() throws IOException {
        HttpSessionWrapper session = new HttpSessionWrapper("123");
        session.setLastAccessedTime(System.currentTimeMillis() - session.getMaxInactiveInterval() * 1000);
        when(sessionStorage.getSession("123")).thenReturn(session);
        HttpSessionWrapper sessionRead = servletContext.getSession("123");
        verify(sessionStorage, times(1)).removeSession(session);
        assertThat(sessionRead, is(nullValue()));
    }

    @Test
    public void shouldCreateSessionWithCorrectContext() {
        HttpSessionWrapper session = servletContext.createNewSession();
        assertThat(session, is(not(nullValue())));
        assertThat(session.getServletContext(), is((ServletContext) servletContext));
    }
}