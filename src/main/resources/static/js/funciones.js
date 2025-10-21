document.addEventListener("DOMContentLoaded", () => {
  const checkbox = document.getElementById("CheckBoxLegal");
  const boton = document.getElementById("ButtonLogin");

  checkbox.addEventListener("change", () => {
    boton.disabled = !checkbox.checked;
    console.log("Checkbox state changed:", checkbox.checked);
  });
});
