function loadReports() {
    var year = document.getElementById("year").value;
    var month = document.getElementById("month").value;
    var auth = {
        "Authorization": "Basic " + btoa(
            sessionStorage.getItem("name") + ":" + sessionStorage.getItem("password")
        )
    };

    fetch("/api/reports/monthly?year=" + year + "&month=" + month, { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (report) {
            if (report) {
                showMonthly(report);
            }
        });

    fetch("/api/reports/expenses-by-category?year=" + year + "&month=" + month, { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (items) {
            if (items) {
                showByCategory(items);
            }
        });

    fetch("/api/reports/accounts", { headers: auth })
        .then(function (response) {
            if (response.ok) {
                return response.json();
            }
        })
        .then(function (items) {
            if (items) {
                showAccounts(items);
            }
        })
}

function showMonthly(report) {
    document.getElementById("monthly").textContent =
        report.income + " " + report.expense + " " + report.net;
}

function showByCategory(items) {
    var box = document.getElementById("by-category");
    box.textContent = "";
    for (var i = 0; i < items.length; i++) {
        box.textContent = box.textContent + items[i].category.name + " " + items[i].amount + "\n";
    }
}

function showAccounts(items) {
    var box = document.getElementById("accounts-report");
    box.textContent = "";
    for (var i = 0; i < items.length; i++) {
        box.textContent = box.textContent + items[i].account + " " + items[i].balance + "\n";
    }
}

document.getElementById("report-button").addEventListener("click", function (event){
    event.preventDefault();
    loadReports();
})

function logout() {
    sessionStorage.clear();
    window.location.href = "/index.html";
}
document.getElementById("logout-button").addEventListener("click", logout);