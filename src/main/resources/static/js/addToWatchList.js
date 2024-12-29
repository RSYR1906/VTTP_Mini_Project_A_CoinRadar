function addToWatchlist(event) {
    const cryptoId = event.target.getAttribute("data-id");
    const name = event.target.getAttribute("data-name");
    const symbol = event.target.getAttribute("data-symbol");
    const currentPrice = event.target.getAttribute("data-price");
    const marketCap = event.target.getAttribute("data-marketcap");
    const priceChangePercentage24h =
      event.target.getAttribute("data-change");
    const logoUrl = event.target.getAttribute("data-logo");

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
