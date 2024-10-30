function nextPage(maxPage){
    let params = new URLSearchParams(window.location.search);
    let selectedPage = params.get("page");
    if (selectedPage == null){
        selectedPage = 0;
    }
    selectedPage = parseInt(selectedPage);
    if(selectedPage !== (maxPage - 1)){
        selectedPage++;
        params.set("page", selectedPage.toString());
        window.location.search = params;
    }
}

function setPage(maxPage, page){
    page--;
    let params = new URLSearchParams(window.location.search);
    let selectedPage = params.get("page");
    if (selectedPage == null){
        selectedPage = 0;
    }
    if(page === parseInt(selectedPage)){
        return;
    }
    params.set("page", page.toString());
    window.location.search = params;
}

function previousPage(){
    let params = new URLSearchParams(window.location.search);
    let selectedPage = params.get("page");
    if (selectedPage == null){
        selectedPage = 0;
    }
    selectedPage = parseInt(selectedPage);
    if(selectedPage !== 0){
        selectedPage--;
        params.set("page", selectedPage.toString());
        window.location.search = params;
    }
}

function checkButtonPages(page, maxPage){
    if (page === "1") {
        document.getElementById("previousPage").className = "grayButton";
    }
    if (page === maxPage) {
        document.getElementById("nextPage").className = "grayButton";
    }
}
