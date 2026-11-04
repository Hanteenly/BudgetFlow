var categoriesCache = [];
var accountsCache = [];
function findCategoryIdByName(name) {
    for (var i = 0; i < categoriesCache.length; i++) {
        if (categoriesCache[i].name === name) {
            return categoriesCache[i].id;
        }
    }
    return null;
}
function findAccountIdByName(name){
    for (var i = 0; i < accountsCache.length; i++) {
        if(accountsCache[i].name === name){
            return accountsCache[i].id;
        }
    }
    return null;
}
function register_transaction(){
    var name = sessionStorage.getItem("name");
    var password = sessionStorage.getItem("password");
    var amount = document.getElementById("amount").value;
    var type = document.getElementById("type").value;
    var accountId = findAccountIdByName(document.getElementById("account-name").value);
    var categoryId = findCategoryIdByName(document.getElementById("category").value);
    if (!accountId) {
        alert("No such account");
        return;
    }
    if (!categoryId) {
        alert("No such category");
        return;
    }
    fetch("/api/transactions", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Basic " +
                btoa(
                    name
                    + ":" +
                    password
                )
        },
        body: JSON.stringify({
            amount: amount,
            type: type,
            createdAt: new Date().toISOString().slice(0, 19),
            category: {
                id: categoryId
            },
            account: {
                id: accountId
            }
        })
    }).then(function (response){
        if(response.ok) {
            load_transaction();
        } else if(response.status === 401){
            window.location.href = "/index.html";
        } else if(response.status === 400){
            alert("Not enough money");
        } else {
            alert(response.status);
        }
    });
}
function load_transaction(){
    fetch("/api/transactions", {
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

    }).then(function (transactions) {
        if (transactions) {
            var box = document.getElementById("transaction-list");
            box.textContent = "";
            for (var i = 0; i < transactions.length; i++) {
                var transaction = transactions[i];
                var tile = document.createElement("div");
                tile.className = "tile";
                var title = document.createElement("strong");
                title.textContent = transaction.category && transaction.category.name
                    ? transaction.category.name
                    : "Transfer";
                tile.appendChild(title);
                var line = document.createElement("div");
                line.textContent = transaction.amount + " " + transaction.type + " " + transaction.account.currency;
                tile.appendChild(line);
                var meta = document.createElement("div");
                meta.className = "muted";
                var when = formatWhen(transaction.createdAt);
                meta.textContent = when
                    ? when + " · " + transaction.account.name
                    : transaction.account.name;
                tile.appendChild(meta);
                box.appendChild(tile);
            }
        }
    });
}

function formatWhen(value) {
    if (!value) {
        return "";
    }
    if (value instanceof Array) {
        var y = value[0];
        var m = String(value[1]).padStart(2, "0");
        var d = String(value[2]).padStart(2, "0");
        var h = String(value[3] || 0).padStart(2, "0");
        var min = String(value[4] || 0).padStart(2, "0");
        return y + "-" + m + "-" + d + " " + h + ":" + min;
    }
    return String(value).replace("T", " ").slice(0, 16);
}
function transfer(){
    var fromId = findAccountIdByName(document.getElementById("source-account").value);
    var toId = findAccountIdByName(document.getElementById("destination-account").value);
    var amount = document.getElementById("transfer-amount").value;
    if (!fromId || !toId) {
        alert("No such account");
        return;
    }

    fetch("/api/transactions/transfer?sourceAccountId=" + fromId
        + "&destinationAccountId=" + toId
        + "&amount=" + amount, {
        method: "POST",
        headers: {
            "Authorization": "Basic " + btoa(
                sessionStorage.getItem("name") + ":" + sessionStorage.getItem("password")
            )
        }
    }).then(function (response){
        if (response.ok) {
            load_transaction();
        }else if (response.status === 401) {
            window.location.href = "/index.html";
        }else if (response.status === 400) {
            alert("Transfer failed");
        }else{
            response.text().then(function (text) {
                alert(text || ("Error: " + response.status));
            });
        }
    })
}
function register_category(){
    var name = document.getElementById("category-name").value;

    fetch("/api/category", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        },
        body: JSON.stringify({
            name: name
        })
    }).then(function (response){
        if(response.ok){
            load_categoryes();
            alert("Category was added");
        }else{
            window.location.href = "/index.html";
        }
    })
}

function load_categoryes(){
    fetch("/api/category", {
        headers: {
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        }
    }).then(function (response){
        if(response.ok){
            return response.json();
        }else if (response.status === 401) {
            alert("This name or password not found");
            window.location.href = "/index.html";
        } else {
            alert("Error: " + response.status);
            window.location.href = "/index.html";
        }
    }).then(function (categoryes){
        if(categoryes){
            var box = document.getElementById("categoryes");
            box.textContent = "";
            for (var i = 0; i < categoryes.length; i++) {
                if (categoryes[i].name === "TRANSFER") {
                    continue;
                }
                var tile = document.createElement("div");
                tile.className = "tile tile-chip";
                tile.textContent = categoryes[i].name;
                box.appendChild(tile);
            }
        }
        categoriesCache = categoryes;
    })
}

function load_accounts(){
    fetch("/api/accounts", {
        headers: {
            "Authorization": "Basic " +
                btoa(
                    sessionStorage.getItem("name")
                    + ":" +
                    sessionStorage.getItem("password")
                )
        }
    }).then(function (response){
        if(response.ok){
            return response.json();
        }
        return [];
    }).then(function (accounts){
        if(accounts){
            accountsCache = accounts;
        }
    });
}

document.getElementById("category-button").addEventListener("click", function (event){
    event.preventDefault();
    register_category();
})
load_categoryes();
load_accounts();

document.getElementById("transfer-button").addEventListener("click", function (event){
    event.preventDefault();
    transfer();
})
function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);

function setType(type) {
    document.getElementById("type").value = type;
    document.getElementById("type-income").classList.toggle("is-on", type === "INCOME");
    document.getElementById("type-expense").classList.toggle("is-on", type === "EXPENSE");
}

document.getElementById("type-income").addEventListener("click", function () {
    setType("INCOME");
});
document.getElementById("type-expense").addEventListener("click", function () {
    setType("EXPENSE");
});

function bindToggle(buttonId, moduleId, hideLabel, showLabel) {
    var button = document.getElementById(buttonId);
    if (!button) {
        return;
    }
    button.addEventListener("click", function () {
        var module = document.getElementById(moduleId);
        var hidden = module.classList.toggle("is-hidden");
        this.textContent = hidden ? showLabel : hideLabel;
    });
}

bindToggle("toggle-history", "history-module", "Hide", "Show");

load_transaction();
document.getElementById("transaction-button").addEventListener("click", function (event){
    event.preventDefault();
    register_transaction();
});