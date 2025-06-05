document.addEventListener("DOMContentLoaded", () => {

  // Manejo de la tabla en recepcion.html
  const recepcionTable = document.querySelector("#recepcionTable tbody");

  if (recepcionTable) {
    // Recuperar los datos almacenados en localStorage
    const servicios = JSON.parse(localStorage.getItem("pedidos")) || [];

    // Verificar si hay servicios almacenados
    if (servicios.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td colspan="11" class="text-center">No hay servicios registrados.</td>
      `;
      recepcionTable.appendChild(row);
    } else {
      // Iterar sobre los servicios y agregarlos a la tabla
      servicios.forEach((servicio) => {
        const row = document.createElement("tr");

        row.innerHTML = `
          <td>${servicio.cliente || "---"}</td>
          <td>${servicio.servicio || "---"}</td>
          <td>${new Date().toLocaleDateString()}</td>
          <td>${servicio.descripcion || "Sin descripción"}</td>
          <td><input type="checkbox" disabled></td>
          <td>${servicio.telefono || "---"}</td>
          <td>${servicio.empresa || "---"}</td>
          <td>${servicio.ruc || "---"}</td>
          <td>${servicio.telefono || "---"}</td>
          <td>${servicio.direccion || "---"}</td>
          <td>En proceso</td>
        `;

        recepcionTable.appendChild(row);
      });
    }
  }

  // Manejo de informes.html
  const historialInformes = document.getElementById("historialInformes");
  const vistaPrevia = document.getElementById("vistaPrevia");
  const informeNombreInput = document.getElementById("informeNombre");
  const grupoSection = document.getElementById("grupoSection");
  const guardarSection = document.getElementById("guardarSection");

  let informe = { nombre: "", grupos: [] };
  let editandoGrupoIndex = null; // Índice del grupo que se está editando

  function crearInforme() {
    const nombre = informeNombreInput.value.trim();
    if (!nombre) return alert("Por favor ingresa un nombre para el informe.");
    informe = { nombre, grupos: [] };
    grupoSection.classList.remove("d-none");
    guardarSection.classList.remove("d-none");
    vistaPrevia.innerHTML = "";
    alert("Informe creado. Ahora puedes agregar grupos de imágenes.");
  }

  function agregarGrupo() {
    const grupoNombre = document.getElementById("grupoNombre").value.trim();
    const imagenesInput = document.getElementById("imagenesGrupo");
    const imagenes = [];

    if (!grupoNombre) return alert("Por favor ingresa un nombre para el grupo.");
    if (imagenesInput.files.length === 0) return alert("Por favor selecciona al menos una imagen.");

    const archivos = Array.from(imagenesInput.files);
    let cargadas = 0;

    archivos.forEach((file) => {
      const reader = new FileReader();
      reader.onload = function (e) {
        imagenes.push(e.target.result);
        cargadas++;
        if (cargadas === archivos.length) {
          const nuevoGrupo = { nombreGrupo: grupoNombre, imagenes };

          if (editandoGrupoIndex !== null) {
            // Reemplazar el grupo existente
            informe.grupos[editandoGrupoIndex] = nuevoGrupo;
            editandoGrupoIndex = null; // Reiniciar el índice de edición
            alert("Grupo editado exitosamente.");
          } else {
            // Agregar un nuevo grupo
            informe.grupos.push(nuevoGrupo);
            alert("Grupo agregado exitosamente.");
          }

          mostrarVistaPrevia();
          document.getElementById("grupoNombre").value = "";
          document.getElementById("imagenesGrupo").value = "";
        }
      };
      reader.readAsDataURL(file);
    });
  }

  function mostrarVistaPrevia() {
    vistaPrevia.innerHTML = "";
    informe.grupos.forEach((grupo, index) => {
      const grupoCard = document.createElement("div");
      grupoCard.className = "card mb-3";
      grupoCard.innerHTML = `
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center">
            <h5 class="card-title mb-0">${grupo.nombreGrupo}</h5>
            <button class="btn btn-sm btn-outline-primary" onclick="editarGrupo(${index})">✏️</button>
          </div>
          <div class="d-flex flex-wrap gap-2 mt-3">
            ${grupo.imagenes.map((img) => `<img src="${img}" class="img-thumbnail">`).join("")}
          </div>
        </div>`;
      vistaPrevia.appendChild(grupoCard);
    });
  }

  function editarGrupo(index) {
    const grupo = informe.grupos[index];
    if (!grupo) return alert("No se encontró el grupo para editar.");

    // Rellenar el formulario con los datos del grupo seleccionado
    document.getElementById("grupoNombre").value = grupo.nombreGrupo;
    document.getElementById("imagenesGrupo").value = ""; // No se puede prellenar un input de tipo file

    // Establecer el índice del grupo que se está editando
    editandoGrupoIndex = index;

    alert(`Editando el grupo: "${grupo.nombreGrupo}". Realiza los cambios y vuelve a agregarlo.`);
  }

  function guardarInforme() {
    if (!informe.nombre || informe.grupos.length === 0)
      return alert("Debes crear un informe y agregar al menos un grupo.");
    let informes = JSON.parse(localStorage.getItem("informesGuardados")) || [];
    informes.push(informe);
    localStorage.setItem("informesGuardados", JSON.stringify(informes));
    alert("Informe guardado exitosamente.");
    informe = { nombre: "", grupos: [] };
    informeNombreInput.value = "";
    grupoSection.classList.add("d-none");
    guardarSection.classList.add("d-none");
    vistaPrevia.innerHTML = "";
    cargarHistorial();
  }

  function cargarHistorial() {
    if (!historialInformes) return;
    historialInformes.innerHTML = "";
    const informes = JSON.parse(localStorage.getItem("informesGuardados")) || [];
    informes.forEach((informe, index) => {
      const item = document.createElement("li");
      item.className = "list-group-item d-flex justify-content-between align-items-center";
      item.innerHTML = `
        <span>${informe.nombre}</span>
        <div>
          <button class="btn btn-sm btn-outline-primary me-2" onclick="cargarInforme(${index})">✏️ Editar</button>
          <button class="btn btn-sm btn-outline-danger" onclick="borrarInforme(${index})">🗑️ Borrar</button>
        </div>
      `;
      historialInformes.appendChild(item);
    });
  }

  function borrarInforme(index) {
    const informes = JSON.parse(localStorage.getItem("informesGuardados")) || [];
    if (index < 0 || index >= informes.length) return alert("Índice inválido.");
    const confirmacion = confirm(`¿Estás seguro de que deseas borrar el informe "${informes[index].nombre}"?`);
    if (confirmacion) {
      informes.splice(index, 1); // Eliminar el informe del array
      localStorage.setItem("informesGuardados", JSON.stringify(informes)); // Actualizar localStorage
      alert("Informe borrado exitosamente.");
      cargarHistorial(); // Actualizar la lista de informes
    }
  }

  function cargarInforme(index) {
    const informes = JSON.parse(localStorage.getItem("informesGuardados")) || [];
    const selectedInforme = informes[index];
    informe = selectedInforme;
    mostrarVistaPrevia();
    grupoSection.classList.remove("d-none");
    guardarSection.classList.remove("d-none");
    informeNombreInput.value = selectedInforme.nombre;
    alert(`Informe "${selectedInforme.nombre}" cargado para edición.`);
  }

  cargarHistorial();

  // Asignar funciones a botones
  window.crearInforme = crearInforme;
  window.agregarGrupo = agregarGrupo;
  window.guardarInforme = guardarInforme;
  window.editarGrupo = editarGrupo;
  window.cargarInforme = cargarInforme;
  window.borrarInforme = borrarInforme;
});

