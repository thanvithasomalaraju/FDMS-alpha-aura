(function () {
  'use strict';

  class ApiClient {
    constructor(baseUrl) {
      this.baseUrl = baseUrl || (window.API_BASE_URL || 'http://localhost:8080/api');
    }

    setBaseUrl(url) {
      this.baseUrl = url;
    }

    getToken() {
      return localStorage.getItem('jwt_token');
    }

    setToken(token) {
      localStorage.setItem('jwt_token', token);
    }

    async request(path, { method = 'GET', headers = {}, body = null } = {}) {
      const url = this.baseUrl + path;
      const h = Object.assign({}, headers);
      const token = this.getToken();
      if (token) h['Authorization'] = 'Bearer ' + token;

      const options = { method, headers: h, body };

      const res = await fetch(url, options);
      const contentType = res.headers.get('content-type') || '';

      let data;
      if (contentType.includes('application/json')) data = await res.json();
      else data = await res.text();

      if (!res.ok) {
        const err = new Error('Request failed');
        err.status = res.status;
        err.body = data;
        throw err;
      }

      return data;
    }

    get(path) {
      return this.request(path, { method: 'GET' });
    }

    postJson(path, json) {
      return this.request(path, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(json) });
    }

    postForm(path, formData) {
      // Note: DO NOT set Content-Type for multipart/form-data; browser will set it including boundary
      return this.request(path, { method: 'POST', body: formData });
    }

    putJson(path, json) {
      return this.request(path, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(json) });
    }

    delete(path) {
      return this.request(path, { method: 'DELETE' });
    }
  }

  // Expose a single shared ApiClient instance
  window.ApiClient = new ApiClient();

})();
