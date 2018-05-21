function checkIfEmpty(){
    user = document.getElementById('username').value;
    password = document.getElementById('password').value;



    if(user === "" || password === "") {
        if(document.getElementById('danelogowanieField')!=null)
            document.getElementById('danelogowanieField').innerHTML = "Podaj nazwę użytkownika i hasło";
        else if(document.getElementById('oczekiwanieField')!=null)
            document.getElementById('oczekiwanieField').innerHTML = "Podaj nazwę użytkownika i hasło";
        else
            document.getElementById('errorField').innerHTML = "Podaj nazwę użytkownika i hasło";
        return false;
    }

    return true;
}

function checkWorkerRegistration(){

    login = document.getElementById('login').value;
    password = document.getElementById('password').value;
    nameE = document.getElementById('name').value;
    surname = document.getElementById('surname').value;
    mail = document.getElementById('mail').value;
    telephone = document.getElementById('telephone').value;
    category = document.getElementById('category').value;

    if(login === '' || password === '' || nameE === '' || surname === '' || mail === '' || telephone === '' || category === ''){
        if(document.getElementById('occupiedLogin')!=null) {
            document.getElementById('occupiedLogin').innerHTML = "Wypełnij wszystkie pola";
        }
        else if(document.getElementById('invalidData')!=null) {
            document.getElementById('invalidData').innerHTML = "Wypełnij wszystkie pola";
        }
        else if(document.getElementById('confirmation')!=null) {
            document.getElementById('confirmation').innerHTML = "Wypełnij wszystkie pola";
        }
        else {
            document.getElementById('notAllData').innerHTML = "Wypełnij wszystkie pola";
        }
        return false;
    }

    return true;
}

function checkInhabitantRegistration(){

    login = document.getElementById('login').value;
    password = document.getElementById('password').value;
    nameE = document.getElementById('name').value;
    surname = document.getElementById('surname').value;
    mail = document.getElementById('mail').value;
    status = document.getElementById('status').value;
    roomnumber = document.getElementById('roomnumber').value;

    if(login === '' || password === '' || nameE === '' || surname === '' || mail === '' || status === '' || roomnumber === ''){
        if(document.getElementById('occupiedLogin')!=null) {
            document.getElementById('occupiedLogin').innerHTML = "Wypełnij wszystkie pola";
        }
        else if(document.getElementById('invalidData')!=null) {
            document.getElementById('invalidData').innerHTML = "Wypełnij wszystkie pola";
        }
        else if(document.getElementById('confirmation')!=null) {
            document.getElementById('confirmation').innerHTML = "Wypełnij wszystkie pola";
        }
        else {
            document.getElementById('notAllData').innerHTML = "Wypełnij wszystkie pola";
        }
        return false;
    }

    return true;
}