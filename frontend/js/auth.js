// Simple auth helper using fetch. Stores JWT in localStorage under key 'jwt_token'.
// Usage:
// Auth.login(username, password).then(res => ...)

const Auth = (() => {
  const BASE = '/api';
  const tokenKey = 'jwt_token';

  function setToken(t) {
    if (t) {
      localStorage.setItem(tokenKey, t);
    } else {
      localStorage.removeItem(tokenKey);
    }
  }

  function getToken() {
    return localStorage.getItem(tokenKey);
  }

  async function register(username, password) {
    const res = await fetch(BASE + '/auth/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    return res.json();
  }

  async function login(username, password) {
    const res = await fetch(BASE + '/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const json = await res.json();
    if (res.ok && json.token) {
      setToken(json.token);
    }
    return json;
  }

  function logout() {
    setToken(null);
  }

  return { setToken, getToken, register, login, logout };
})();

// Expose globally
window.Auth = Auth;
