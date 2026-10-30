function register_transaction(){
    var name = sessionStorage.getItem("name");
    var password = sessionStorage.getItem("password");
    var amount = document.getElementById("amount").value;
    var type = document.getElementById("type").value;
    var accountId = document.getElementById("account-id").value;
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
            document.getElementById("transaction-list").textContent = "";
            for (let i = 0; i < transactions.length; i++) {
                const transaction = transactions[i];
                const box = document.getElementById("transaction-list");
                box.textContent = box.textContent + transaction.amount + " " + transaction.type + " " + transaction.account.currency + " " + transaction.createdAt + " " + transaction.account.name + "\n";
            }
        }
    });
}
function transfer(){
    var fromId = document.getElementById("sourceAccountId").value;
    var toId = document.getElementById("destinationAccountId").value;
    var amount = document.getElementById("transfer-amount").value;

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
            alert("Error: " + response.status);
        }
    })
}
document.getElementById("transfer-button").addEventListener("click", function (event){
    event.preventDefault();
    transfer();
})
function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);

load_transaction();
document.getElementById("transaction-button").addEventListener("click", function (event){
    event.preventDefault();
    register_transaction();
});