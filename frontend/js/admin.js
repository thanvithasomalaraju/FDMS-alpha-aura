// Admin UI helpers: fetch delivery applications and render them into the page.
// Requires admin JWT stored by Auth (localStorage 'jwt_token').

const AdminUI = (() => {
  const BASE = '/api';

  function authHeaders() {
    const token = localStorage.getItem('jwt_token');
    return token ? { 'Authorization': 'Bearer ' + token } : {};
  }

  async function fetchApplications() {
    const res = await fetch(BASE + '/delivery/partners/applications', {
      method: 'GET',
      headers: { ...authHeaders(), 'Accept': 'application/json' }
    });
    if (!res.ok) throw new Error('Failed to fetch applications: ' + res.status);
    return res.json();
  }

  async function fetchApplication(id) {
    const res = await fetch(BASE + `/delivery/partners/applications/${id}`, {
      method: 'GET',
      headers: { ...authHeaders(), 'Accept': 'application/json' }
    });
    if (!res.ok) throw new Error('Failed to fetch application: ' + res.status);
    return res.json();
  }

  function renderList(containerId, apps) {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = '';
    const table = document.createElement('table');
    table.className = 'admin-table';
    const thead = document.createElement('thead');
    thead.innerHTML = '<tr><th>ID</th><th>Name</th><th>Phone</th><th>Submitted</th><th>Actions</th></tr>';
    table.appendChild(thead);
    const tbody = document.createElement('tbody');
    apps.forEach(a => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${a.id}</td><td>${escapeHtml(a.fullName)}</td><td>${escapeHtml(a.phone)}</td><td>${a.createdAt || ''}</td>`;
      const actionsTd = document.createElement('td');
      const viewBtn = document.createElement('button');
      viewBtn.textContent = 'View';
      viewBtn.onclick = () => loadAndShow(a.id);
      actionsTd.appendChild(viewBtn);
      tr.appendChild(actionsTd);
      tbody.appendChild(tr);
    });
    table.appendChild(tbody);
    container.appendChild(table);
  }

  function escapeHtml(s) {
    if (!s) return '';
    return s.replace(/[&<>"']/g, function (c) {
      return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c];
    });
  }

  async function loadAndShow(id) {
    try {
      const app = await fetchApplication(id);
      await showModal(app);
    } catch (err) {
      alert('Error: ' + err.message);
    }
  }

  async function getDownloadUrl(key) {
    const token = localStorage.getItem('jwt_token');
    const headers = token ? { 'Authorization': 'Bearer ' + token } : {};
    const res = await fetch(BASE + '/storage/download?key=' + encodeURIComponent(key), {
      method: 'GET',
      headers: headers
    });
    if (!res.ok) throw new Error('Failed to get download URL: ' + res.status);
    const json = await res.json();
    if (!json || !json.success) throw new Error('Failed to get download URL');
    return json.downloadUrl;
  }

  async function showModal(app) {
    // Very simple modal via alert for now; recommend replacing with a nicer UI
    let msg = `Application #${app.id}\nName: ${app.fullName}\nPhone: ${app.phone}\nSource: ${app.source}\n`;
    if (app.photoPath) msg += `Photo: ${app.photoPath}\n`;
    if (app.licensePath) msg += `License: ${app.licensePath}\n`;
    if (app.rcPath) msg += `RC: ${app.rcPath}\n`;
    if (app.aadharPath) msg += `Aadhar: ${app.aadharPath}\n`;
    const proceed = confirm(msg + '\nOpen file links in new tabs?');
    if (proceed) {
      const keys = [app.photoPath, app.licensePath, app.rcPath, app.aadharPath];
      for (const k of keys) {
        if (!k) continue;
        try {
          // If the path looks like an S3 key, request a presigned download URL from the backend
          const downloadUrl = await getDownloadUrl(k);
          window.open(downloadUrl, '_blank');
        } catch (err) {
          console.warn('Failed to fetch download URL for', k, err);
          // Fallback: try the legacy file endpoint
          window.open('/api/delivery/partners/files/' + encodeURIComponent(k), '_blank');
        }
      }
    }
  }

  return { fetchApplications, renderList };
})();

// Expose globally
window.AdminUI = AdminUI;
