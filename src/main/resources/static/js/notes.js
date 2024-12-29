function editNote(button) {
  const cryptoId = button.getAttribute("data-id");
  const noteDisplay = document.getElementById(`note-display-${cryptoId}`);
  const currentNote = noteDisplay.querySelector("span").textContent.trim();

  // Replace the display with a textarea for editing
  noteDisplay.outerHTML = `
    <div id="note-edit-${cryptoId}">
      <textarea
        id="note-${cryptoId}"
        class="form-control"
        rows="2"
        placeholder="Edit your notes"
      >${currentNote}</textarea>
      <button
        class="btn btn-outline-success mt-2"
        data-id="${cryptoId}"
        onclick="saveNote(this)"
      >
        Save
      </button>
    </div>
  `;
}

function saveNote(button) {
  const cryptoId = button.getAttribute("data-id");
  const noteTextarea = document.getElementById(`note-${cryptoId}`);
  const noteText = noteTextarea.value.trim();

  if (!noteText) {
    alert("Note cannot be empty.");
    return;
  }

  // Show a loading spinner while saving
  const originalText = button.innerHTML;
  button.innerHTML =
    '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>';
  button.disabled = true;

  fetch("/user/watchlist/update-note", {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: new URLSearchParams({ id: cryptoId, note: noteText }),
  })
    .then((response) => {
      if (response.ok) {
        // Replace the textarea with the updated note and pencil icon
        const noteDiv = `
          <div id="note-display-${cryptoId}">
            <span>${noteText}</span>
            <button
              class="btn btn-link p-0 text-decoration-none"
              data-id="${cryptoId}"
              onclick="editNote(this)"
            >
              ✏️
            </button>
          </div>
        `;
        noteTextarea.parentElement.outerHTML = noteDiv;
        alert("Note saved successfully!");
      } else {
        alert("Failed to save the note. Please try again.");
      }
    })
    .catch((error) => console.error("Error:", error))
    .finally(() => {
      // Reset the button
      button.innerHTML = originalText;
      button.disabled = false;
    });
}