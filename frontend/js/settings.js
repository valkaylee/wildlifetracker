window.onload = function() {
  loadSaved();
};

function loadSaved() {
  const name = localStorage.getItem("name") || "";
  const bio = localStorage.getItem("bio") || "";
  const role = localStorage.getItem("role") || "usc_undergrad";
  const location = localStorage.getItem("location") || "la";

  // 填入输入框
  document.getElementById("nameInput").value = name;
  document.getElementById("bioInput").value = bio;
  document.getElementById("roleSelect").value = role;
  document.getElementById("locationSelect").value = location;

  // 显示区
  document.getElementById("pName").innerText = name || "(none)";
  document.getElementById("pBio").innerText = bio || "(none)";
  document.getElementById("pRole").innerText = formatRole(role);
  document.getElementById("pLocation").innerText = formatLocation(location);
}

function saveSettings() {
  const name = document.getElementById("nameInput").value.trim();
  const bio = document.getElementById("bioInput").value.trim();
  const role = document.getElementById("roleSelect").value;
  const location = document.getElementById("locationSelect").value;

  localStorage.setItem("name", name);
  localStorage.setItem("bio", bio);
  localStorage.setItem("role", role);
  localStorage.setItem("location", location);

  loadSaved();

  alert("Settings saved!");
}

function formatRole(r) {
  switch(r) {
    case "usc_undergrad": return "USC Student (Undergraduate)";
    case "usc_graduate": return "USC Student (Graduate)";
    case "usc_faculty": return "USC Faculty";
    case "usc_staff": return "USC Staff";
    case "tourist": return "Tourist";
    case "resident": return "Local Resident";
    default: return "Other";
  }
}

function formatLocation(l) {
  switch(l) {
    case "la": return "Los Angeles";
    case "pasadena": return "Pasadena";
    case "irvine": return "Irvine";
    case "sandiego": return "San Diego";
    case "bayarea": return "Bay Area";
    case "other_ca": return "Other Cities in California";
    case "outside_ca": return "Outside California";
    default: return "(none)";
  }
}
