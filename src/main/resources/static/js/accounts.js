    fetch("/api/accounts", {
        headers: {
            "Authorization" : "Basic " +
                btoa(
                sessionStorage.getItem("name")
                + ":" +
                sessionStorage.getItem("password")
                )
        }
    })
        .then(function (response) {
            if(response.ok){
                return response.json();
            }else if(response.status === 401){
                alert("This name or password not found");
                window.location.href = "/index.html";
            }else {
                alert("Error: " + response.status);
                window.location.href = "/index.html";
            }

        })
        .then(function (accounts) {
            if(accounts){
                for(let i = 0; i < accounts.length; i++){
                    const account = accounts[i];
                    const box = document.getElementById("accounts");
                    box.textContent = box.textContent + account.name + " " + account.balance + " " + account.currency + "\n";
                }
            }
        });

document.getElementById("account").addEventListener("submit", function (event){
    event.preventDefault();
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
            window.location.reload()
        }
    })
});