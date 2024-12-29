async function fetchLiveCryptoData() {
    try {
      console.log("Fetching live crypto data...");
      const response = await fetch("/api/cryptos");
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
      const data = await response.json();

      // Update the table dynamically
      const rows = document.querySelectorAll('[data-id="crypto-row"]');
      rows.forEach((row, index) => {
        if (data[index]) {
          const crypto = data[index];
          console.log("Processing crypto:", crypto); // Log each crypto object

          // Helper function to update values with highlight effect
          const updateWithHighlight = (selector, newValue) => {
            const element = row.querySelector(selector);

            // Skip highlighting for the rank
            if (selector === '[data-id="crypto-rank"]') {
              element.textContent = newValue; // Just update the value
              return;
            }
            if (element.textContent !== newValue) {
              console.log(
                `Updating ${selector} from "${element.textContent}" to "${newValue}"`
              );
              element.textContent = newValue;
              element.classList.add("highlight-update");
              setTimeout(() => {
                console.log(`Removing highlight from ${selector}`);
                element.classList.remove("highlight-update");
              }, 1000); // Highlight for 1 second
            }
          };

          // Update rank
          updateWithHighlight('[data-id="crypto-rank"]', index + 1);

          // Update name
          updateWithHighlight(
            '[data-id="crypto-name"]',
            crypto.name || "N/A"
          );

          // Update symbol
          updateWithHighlight(
            '[data-id="crypto-symbol"]',
            crypto.symbol?.toUpperCase() || "N/A"
          );

          // Update current price
          updateWithHighlight(
            '[data-id="crypto-price"]',
            "$" +
              crypto.current_price.toLocaleString(undefined, {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2,
              })
          );

          // Update market cap
          updateWithHighlight(
            '[data-id="crypto-market-cap"]',
            "$" +
              crypto.market_cap.toLocaleString(undefined, {
                minimumFractionDigits: 0,
                maximumFractionDigits: 0,
              })
          );

          // Update price change percentage
          const priceChangeCell = row.querySelector(
            '[data-id="crypto-price-change"]'
          );
          const newPriceChange =
            crypto.price_change_percentage_24h.toFixed(2) + "%";
          if (priceChangeCell.textContent !== newPriceChange) {
            priceChangeCell.textContent = newPriceChange;
            priceChangeCell.classList.remove("text-success", "text-danger");
            priceChangeCell.classList.add(
              crypto.price_change_percentage_24h > 0
                ? "text-success"
                : "text-danger"
            );
            priceChangeCell.classList.add("highlight-update");
            setTimeout(() => {
              priceChangeCell.classList.remove("highlight-update");
            }, 1000); // Remove highlight after 1 second
          }
        }
      });
      console.log("Table updated successfully.");
    } catch (error) {
      console.error("Error fetching live crypto data:", error);
    }
  }

  // Fetch data every 60 seconds
  setInterval(fetchLiveCryptoData, 60000);
  fetchLiveCryptoData(); // Initial fetch