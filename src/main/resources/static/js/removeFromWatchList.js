function removeFromWatchlist(event) {
    const username = "[[${username}]]";
    const button = event.target; // Fix: Use event.target to get the clicked button
    const cryptoId = button.getAttribute("data-id");

    // Confirm before deleting
    const confirmDelete = confirm(
      "Are you sure you want to remove this item from your watchlist?"
    );
    if (!confirmDelete) return;

    // Show a loading spinner while processing
    const originalText = button.innerHTML;
    button.innerHTML =
      '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
    button.disabled = true;

    // Make an AJAX call to remove the crypto from the watchlist
    fetch("https://coinradar.up.railway.app/user/watchlist/remove", { 
    method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        username: username,
        id: cryptoId,
    }),
    })
      .then((response) => {
        if (response.ok) {
          // Remove the row from the table without reloading
          const row = button.closest("tr");
          row.remove();
          window.location.reload();
        } else {
          alert("Failed to remove item from watchlist. Please try again.");
        }
      })
      .catch((error) => console.error("Error:", error))
      .finally(() => {
        // Reset the button
        button.innerHTML = originalText;
        button.disabled = false;
      });
  }