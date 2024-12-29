function addToWatchlist(event) {
    const button = event.target;
    const cryptoId = button.getAttribute("data-id");
    const name = button.getAttribute("data-name");
    const symbol = button.getAttribute("data-symbol");
    const currentPrice = button.getAttribute("data-price");
    const marketCap = button.getAttribute("data-marketcap");
    const priceChangePercentage24h = button.getAttribute("data-change");
    const logoUrl = button.getAttribute("data-logo");

    // Make the POST request
    fetch("/watchlist/add", {
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
          // Change the button's background color to green and disable it
          button.style.backgroundColor = "green";
          button.style.color = "white";
          button.disabled = true;
          button.textContent = "Added ✔";
          alert("Added to watchlist!");
        } else {
          alert("Failed to add to watchlist.");
        }
      })
      .catch((error) => console.error("Error:", error));
  }
