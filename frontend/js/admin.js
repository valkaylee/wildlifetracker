// Admin Dashboard JavaScript

const API_BASE = 'http://localhost:8080/api';

// Load all dashboard data
async function loadDashboard() {
  try {
    // Load sightings and calculate statistics
    const sightingsResponse = await fetch(`${API_BASE}/sightings`);
    const sightings = await sightingsResponse.json();
    
    // Load users
    let users = [];
    try {
      const usersResponse = await fetch(`${API_BASE}/users`);
      if (usersResponse.ok) {
        const usersData = await usersResponse.json();
        users = Array.isArray(usersData) ? usersData : [];
      }
    } catch (error) {
      console.warn('Could not load users:', error);
      // Continue without users data
    }
    
    // Calculate and display statistics
    displayStatistics(sightings, users);
    
    // Display top 5 species
    displayTopSpecies(sightings);
    
    // Display all sightings in table
    displaySightingsTable(sightings);
    
    // Setup search functionality
    setupSearch(sightings);
    
  } catch (error) {
    console.error('Error loading dashboard:', error);
    showError('Failed to load dashboard data');
  }
}

// Calculate and display statistics
function displayStatistics(sightings, users) {
  // Total sightings
  const totalSightings = sightings.length;
  document.getElementById('totalSightings').textContent = totalSightings;
  
  // Unique species
  const uniqueSpecies = new Set(sightings.map(s => s.species).filter(s => s)).size;
  document.getElementById('uniqueSpecies').textContent = uniqueSpecies;
  
  // Most spotted species
  const speciesCounts = {};
  sightings.forEach(sighting => {
    if (sighting.species) {
      speciesCounts[sighting.species] = (speciesCounts[sighting.species] || 0) + 1;
    }
  });
  
  let mostSpotted = null;
  let mostSpottedCount = 0;
  for (const [species, count] of Object.entries(speciesCounts)) {
    if (count > mostSpottedCount) {
      mostSpotted = species;
      mostSpottedCount = count;
    }
  }
  
  if (mostSpotted) {
    document.getElementById('mostSpotted').textContent = mostSpotted;
    document.getElementById('mostSpottedCount').textContent = `${mostSpottedCount} sightings`;
  } else {
    document.getElementById('mostSpotted').textContent = 'N/A';
    document.getElementById('mostSpottedCount').textContent = '0 sightings';
  }
  
  // Total users
  const totalUsers = Array.isArray(users) ? users.length : 0;
  document.getElementById('totalUsers').textContent = totalUsers;
  
  // Pending reports (for now, we'll set to 0 as it's not implemented yet)
  document.getElementById('pendingReports').textContent = '0';
}

// Display top 5 species
function displayTopSpecies(sightings) {
  const container = document.getElementById('topSpeciesList');
  
  if (sightings.length === 0) {
    container.innerHTML = '<li class="empty-message">No sightings yet</li>';
    return;
  }
  
  // Count species
  const speciesCounts = {};
  sightings.forEach(sighting => {
    if (sighting.species) {
      speciesCounts[sighting.species] = (speciesCounts[sighting.species] || 0) + 1;
    }
  });
  
  // Sort by count and get top 5
  const topSpecies = Object.entries(speciesCounts)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 5);
  
  if (topSpecies.length === 0) {
    container.innerHTML = '<li class="empty-message">No species data available</li>';
    return;
  }
  
  container.innerHTML = topSpecies.map(([species, count]) => 
    `<li>${species} (${count} ${count === 1 ? 'sighting' : 'sightings'})</li>`
  ).join('');
}

// Display sightings in table
function displaySightingsTable(sightings, filteredSightings = null) {
  const tbody = document.getElementById('sightingsTableBody');
  const dataToShow = filteredSightings || sightings;
  
  if (dataToShow.length === 0) {
    tbody.innerHTML = '<tr><td colspan="5" class="empty-message">No sightings found</td></tr>';
    return;
  }
  
  tbody.innerHTML = dataToShow.map(sighting => {
    const date = sighting.timestamp 
      ? new Date(sighting.timestamp).toLocaleDateString('en-US', { year: 'numeric', month: '2-digit', day: '2-digit' })
      : 'N/A';
    const time = sighting.timestamp
      ? new Date(sighting.timestamp).toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false })
      : 'N/A';
    const username = sighting.user ? sighting.user.username : 'Unknown';
    
    return `
      <tr>
        <td>${sighting.species || 'Unknown'}</td>
        <td>${sighting.location || 'Unknown'}</td>
        <td>${date}</td>
        <td>${time}</td>
        <td>${username}</td>
      </tr>
    `;
  }).join('');
}

// Setup search functionality
function setupSearch(sightings) {
  const searchInput = document.getElementById('searchInput');
  let allSightings = sightings;
  
  searchInput.addEventListener('input', (e) => {
    const query = e.target.value.toLowerCase().trim();
    
    if (query === '') {
      displaySightingsTable(allSightings);
      return;
    }
    
    const filtered = allSightings.filter(sighting => {
      const species = (sighting.species || '').toLowerCase();
      const location = (sighting.location || '').toLowerCase();
      const username = (sighting.user?.username || '').toLowerCase();
      
      return species.includes(query) || 
             location.includes(query) || 
             username.includes(query);
    });
    
    displaySightingsTable(allSightings, filtered);
  });
}

// View sighting details
function viewSighting(id) {
  // For now, just show an alert. You can create a modal or detail page later
  alert(`Viewing sighting details for ID: ${id}`);
  // You can implement a modal or redirect to a detail page here
}

// Delete sighting
async function deleteSighting(id) {
  if (!confirm('Are you sure you want to delete this sighting?')) {
    return;
  }
  
  try {
    const response = await fetch(`${API_BASE}/sightings/${id}`, {
      method: 'DELETE'
    });
    
    if (response.ok) {
      showMessage('Sighting deleted successfully', 'success');
      // Reload dashboard
      loadDashboard();
    } else {
      throw new Error('Failed to delete sighting');
    }
  } catch (error) {
    console.error('Error deleting sighting:', error);
    showMessage('Failed to delete sighting', 'error');
  }
}

// Show message
function showMessage(message, type) {
  // Remove existing message if any
  const existingMessage = document.querySelector('.admin-message');
  if (existingMessage) {
    existingMessage.remove();
  }
  
  // Create message element
  const messageEl = document.createElement('div');
  messageEl.className = `admin-message admin-message-${type}`;
  messageEl.textContent = message;
  
  // Insert after header
  const container = document.querySelector('.admin-container');
  const header = document.querySelector('.admin-header');
  container.insertBefore(messageEl, header.nextSibling);
  
  // Remove message after 5 seconds
  setTimeout(() => {
    messageEl.remove();
  }, 5000);
}

// Show error
function showError(message) {
  showMessage(message, 'error');
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
  loadDashboard();
});

