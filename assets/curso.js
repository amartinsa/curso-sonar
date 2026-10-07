/* ============================================================
   SonarQube · Curso 1 h — comportamiento compartido
   Tema, navegación por teclado y modo presentación.
   Sin JS todo sigue funcionando: la navegación son enlaces <a>.
   ============================================================ */
(function () {
  var doc = document.documentElement;

  /* ---------- tema claro/oscuro ---------- */
  var guardado = null;
  try { guardado = localStorage.getItem('sq-theme'); } catch (e) {}
  if (guardado) doc.setAttribute('data-theme', guardado);

  function cambiarTema() {
    var siguiente = doc.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
    doc.setAttribute('data-theme', siguiente);
    try { localStorage.setItem('sq-theme', siguiente); } catch (e) {}
  }
  var bt = document.getElementById('tema');
  if (bt) bt.addEventListener('click', cambiarTema);

  /* ---------- pantalla completa (F) ---------- */
  function pantallaCompleta() {
    if (!document.fullscreenEnabled) return;
    if (document.fullscreenElement) {
      document.exitFullscreen();
    } else {
      doc.requestFullscreen().catch(function () {});
    }
  }
  var bf = document.getElementById('fsBtn');
  if (bf) bf.addEventListener('click', pantallaCompleta);

  /* ---------- enlaces de navegación ---------- */
  var prev = document.querySelector('.nav .prev');
  var next = document.querySelector('.nav .next');

  var body = document.body;
  var aHome = body.getAttribute('data-home') || 'index.html';
  var aFin = body.getAttribute('data-end') || '09-referencias.html';

  function enRango() {
    var t = document.activeElement;
    if (!t) return true;
    var tag = t.tagName;
    return !(tag === 'INPUT' || tag === 'TEXTAREA' || tag === 'SELECT' || t.isContentEditable);
  }

  document.addEventListener('keydown', function (e) {
    if (!enRango() || e.ctrlKey || e.metaKey || e.altKey) return;
    var k = e.key;

    if (k === 'ArrowRight' || k === 'PageDown') {
      if (next) { e.preventDefault(); window.location.href = next.getAttribute('href'); }
    } else if (k === 'ArrowLeft' || k === 'PageUp') {
      if (prev) { e.preventDefault(); window.location.href = prev.getAttribute('href'); }
    } else if (k === 'Home') {
      e.preventDefault(); window.location.href = aHome;
    } else if (k === 'End') {
      e.preventDefault(); window.location.href = aFin;
    } else if (k === 'f' || k === 'F') {
      e.preventDefault(); pantallaCompleta();
    } else if (k === 'Escape' && document.fullscreenElement) {
      document.exitFullscreen();
    }
  });
})();
