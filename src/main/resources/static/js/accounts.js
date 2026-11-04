function loadAccounts() {
    fetch("/api/accounts", {
        headers: {
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        }
    }).then(function (response) {
            if (response.ok) {
                return response.json();
            } else if (response.status === 401) {
                alert("This name or password not found");
                window.location.href = "/index.html";
            } else {
                alert("Error: " + response.status);
                window.location.href = "/index.html";
            }

        }).then(function (accounts) {
            if (accounts) {
                var box = document.getElementById("accounts");
                box.textContent = "";
                for (var i = 0; i < accounts.length; i++) {
                    var account = accounts[i];
                    var tile = document.createElement("div");
                    tile.className = "tile";
                    var title = document.createElement("strong");
                    title.textContent = account.name;
                    tile.appendChild(title);
                    var line = document.createElement("div");
                    line.textContent = account.balance + " " + account.currency;
                    tile.appendChild(line);
                    box.appendChild(tile);
                }
            }
        });
}

function createAccount(){
    fetch("/api/accounts", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization" : "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        },
        body: JSON.stringify({
            name: document.getElementById("name").value,
            balance: document.getElementById("balance").value,
            currency: document.getElementById("currency").value
        })
    }).then(function (responce){
        if (responce.ok){
            window.location.href = "/account.html";
            loadAccounts();
        }
    })
}

function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);

loadAccounts();
document.getElementById("account").addEventListener("submit", function (event) {
    event.preventDefault();
    createAccount();
});