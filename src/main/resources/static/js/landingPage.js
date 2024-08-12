
function clear(){
    document.getElementById("check1").checked = false;
    document.getElementById("check2").checked = false;
    document.getElementById("check3").checked = false;
    document.getElementById("check4").checked = false;
    document.getElementById("check5").checked = false;
}


function checked1(){
    if(document.getElementById("check1").checked === true || document.getElementById("check2").checked === true){
        clear();
        document.getElementById("check1").checked = true;
    }
    else{
        clear();
    }
}

function checked2(){
    if(document.getElementById("check2").checked === true || document.getElementById("check3").checked === true){
        clear();
        document.getElementById("check1").checked = true;
        document.getElementById("check2").checked = true;

    }
    else{
        clear();
    }
}

function checked3(){
    if(document.getElementById("check3").checked === true || document.getElementById("check4").checked === true){
        clear();
        document.getElementById("check1").checked = true;
        document.getElementById("check2").checked = true;
        document.getElementById("check3").checked = true;
    }
    else{
        clear();
    }
}

function checked4(){
    if(document.getElementById("check4").checked === true || document.getElementById("check5").checked === true){
        clear();
        document.getElementById("check1").checked = true;
        document.getElementById("check2").checked = true;
        document.getElementById("check3").checked = true;
        document.getElementById("check4").checked = true;
    }
    else{
        clear();
    }
}

function checked5(){
    if(document.getElementById("check5").checked === true){
        document.getElementById("check1").checked = true;
        document.getElementById("check2").checked = true;
        document.getElementById("check3").checked = true;
        document.getElementById("check4").checked = true;
        document.getElementById("check5").checked = true;
    }
    else{
        clear();
    }
}
