const watchlistButton = document.getElementById('watchlist-button')
export var onWatchListButtonClick = watchlistButton.onclick = function () {
    showWatchlistModal()
    function showWatchlistModal() {
        const modal = document.createElement('div')
        modal.innerHTML += `
        <div class="modal-content">
        <span class="close" onclick="document.body.removeChild(this.parentElement.parentElement)">&times;</span>
        <p>Watchlist Modal</p>
        </div>`;
        modal.className = 'modal'
        modal.style.display = 'block'
        document.body.appendChild(modal)
        document.addEventListener('keydown',closeModalOnEscape)
    }
}
const sellButton = document.getElementById('sell-button')
export var onSellItemButtonClick = sellButton.onclick = function () {
    showSellModal()
    function showSellModal() {
        const modal = document.createElement('div')
        modal.innerHTML += `
        <div class="modal-content">
        <span class="close" onclick="document.body.removeChild(this.parentElement.parentElement)">&times;</span>
        <form action="sellItem.html" method="get" autocomplete="off">
            <h2>Put an item up for auction</h2>
            <label>Image URL</label>
            <input type="text" name="imageURL" placeholder="URL to the item's image">
            <label>Item Name</label>
            <input type="text" name="itemName" placeholder="Your item's name at birth" required>
            <label>Item Description</label>
            <textarea name="itemDescription" cols="10" rows="10" maxlength="150" placeholder="Write something cool about it"></textarea>
            <label>Minimum Bid Value</label>
            <input class="value-input" type="number" name="minimumBidInput" placeholder="0.00">
            <label>Expire Date</label>
            <input type="date" name="expireDate">
            <button class="item-button" type="submit" name="sellFormSubmit">Confirm  <i class="fa fa-paper-plane"></i></button>
        </form>
        </div>`;
        modal.className = 'modal'
        modal.style.display = 'block'
        document.body.appendChild(modal)
        document.addEventListener('keydown',closeModalOnEscape)
    }
}
function closeModalOnEscape(e){
    if(e.key == 'Escape'){
        document.body.removeChild(document.querySelector('.modal'))
        document.removeEventListener('keydown',closeModalOnEscape)
    }
}
export function setItemButtonsOnclick() {
    const offerButtons = document.querySelectorAll('.item-button');
    offerButtons.forEach((button) => {
        switch (button.getAttribute('race')) {
            case 'offer':
                button.onclick = function () {
                    const itemID = button.value;
                    console.log('Offer' + itemID);
                }
                break;
            case 'watchlist':
                button.onclick = function () {
                    const itemID = button.value;
                    console.log('Watch' + itemID);
                }
                break;

        }
    })
}

