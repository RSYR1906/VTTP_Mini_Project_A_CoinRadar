function addToWatchlist(event) {
    const button = event.target;
  
    // Extract data attributes
    const cryptoId = button.getAttribute("data-id");
    const name = button.getAttribute("data-name");
    const symbol = button.getAttribute("data-symbol");
    const currentPrice = button.getAttribute("data-price");
    const marketCap = button.getAttribute("data-marketcap");
    const priceChangePercentage24h = button.getAttribute("data-change");
    const logoUrl = button.getAttribute("data-logo");
  
    // Validate required attributes
    if (!cryptoId || !name || !symbol || !currentPrice || !marketCap || !priceChangePercentage24h || !logoUrl) {
      alert("Invalid data. Unable to add to watchlist.");
      return;
    }
  
    // Show loading indicator
    const originalText = button.textContent;
    button.textContent = "Adding...";
    button.disabled = true;
  
    // Make the POST request
    fetch("/user/watchlist/add", {
      method: "POST",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
      },
      body: new URLSearchParams({
        id: cryptoId,
        name: name,
        symbol: symbol,
        currentPrice: currentPrice,
        marketCap: marketCap,
        priceChangePercentage24h: priceChangePercentage24h,
        logoUrl: logoUrl,
      }),
    })
      .then((response) => {
        if (response.ok) {
          // Success feedback
          button.style.backgroundColor = "green";
          button.style.color = "white";
          button.textContent = "Added ✔";
          alert("Added to watchlist!");
        } else {
          // Restore button state on failure
          button.textContent = originalText;
          button.disabled = false;
          response.text().then((text) => {
            console.error("Error response:", text);
            alert("Failed to add to watchlist. Please try again.");
          });
        }
      })
      .catch((error) => {
        console.error("Error:", error);
        button.textContent = originalText;
        button.disabled = false;
        alert("An error occurred. Please try again.");
      });
  }