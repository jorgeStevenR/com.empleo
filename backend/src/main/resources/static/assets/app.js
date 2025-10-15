/* ===== Utils / sesión ===== */
window.App = (() => {
  const qs = (s) => document.querySelector(s);
  const qsa = (s) => [...document.querySelectorAll(s)];
  const escapeHtml = (s) =>
    String(s || "").replace(/[&<>"']/g, (m) => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;",
    }[m]));
  const fmtDate = (v) => {
    try {
      const d = new Date(v);
      return isNaN(d) ? String(v) : d.toLocaleString();
    } catch {
      return String(v || "");
    }
  };

  function getUser() {
    try {
      return JSON.parse(localStorage.getItem("user") || "null");
    } catch {
      return null;
    }
  }
  function setUser(u) {
    localStorage.setItem("user", JSON.stringify(u || null));
  }
  function logout() {
    localStorage.removeItem("user");
    localStorage.removeItem("token");
    location.replace("/");
  }
  function role() {
    const u = getUser();
    return ((u?.role) ?? "GUEST").toUpperCase();
  }
  function requireRole(...roles) {
    const r = role();
    if (!roles.map((x) => x.toUpperCase()).includes(r)) {
      location.replace("/login.html?next=" + encodeURIComponent(location.pathname + location.search));
    }
  }

  async function fetchJSON(url, opts) {
    const res = await fetch(url, opts);
    if (!res.ok) throw new Error("HTTP " + res.status);
    const ct = (res.headers.get("content-type") || "").toLowerCase();
    if (!ct.includes("application/json")) throw new Error("Respuesta no JSON");
    return res.json();
  }

  function wireHeader() {
    const u = getUser();
    const wel = qs("#welcome");
    const login = qs("#loginLink");
    const logoutBtn = qs("#logoutBtn");
    const emp = qs("#empLink");
    if (!wel && !login && !logoutBtn) return;

    if (u && wel) wel.textContent = `${u.name || u.email} — ${(u.role || "USER").toUpperCase()}`;
    if (login) login.style.display = u ? "none" : "inline-block";
    if (logoutBtn) {
      logoutBtn.style.display = u ? "inline-block" : "none";
      logoutBtn.addEventListener("click", () => logout());
    }
    if (emp && (role() === "EMPLOYER" || role() === "ADMIN")) {
      emp.style.display = "inline-block";
    }
  }

  return { qs, qsa, escapeHtml, fmtDate, getUser, setUser, logout, role, requireRole, fetchJSON, wireHeader };
})();
