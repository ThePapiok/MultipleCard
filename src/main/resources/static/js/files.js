let size = 0;

function setFile(e) {
    let file = e.files[0];
    let message;
    let error = false;
    if (file == null) {
        error = true;
        message = "";
    } else if (file.type !== "application/pdf") {
        error = true;
        message = document.getElementById("textBadType").textContent;
    } else if (file.size >= 5000000) {
        error = true;
        message = document.getElementById("textTooBig").textContent;
    }
    if (error) {
        document.getElementById("errorFile").textContent = message;
        e.value = "";
    } else {
        size++;
        if (size <= 8) {
            let uploadFiles = document.getElementById("uploadFiles");
            let uploadFile = document.createElement("div");
            let input = document.createElement("input");
            check(1, true, ok, previous, buttonId, success, false, true);
            document.getElementById("errorFile").textContent = "";
            uploadFile.className = "uploadFile";
            uploadFile.id = "file" + (size + 1);
            input.type = "file";
            input.className = "inputFile";
            input.name = "file";
            input.oninput = () => setFile(input);
            uploadFile.appendChild(input);
            uploadFiles.appendChild(uploadFile);
            let previousUploadFile = document.getElementById("file" + size);
            previousUploadFile.firstElementChild.hidden = true;
            previousUploadFile.style.zIndex = "1";
            let fileContainer = document.createElement("div");
            let deleteFile = document.createElement("img");
            let imageFile = document.createElement("img");
            let nameFile = document.createElement("span");
            let index = size;
            fileContainer.className = "fileContainer";
            deleteFile.title = document.getElementById("textDeleteFile").textContent;
            deleteFile.src = "/images/close.png";
            deleteFile.alt = "close";
            deleteFile.className = "deleteFile";
            deleteFile.onclick = () => deleteFiles(previousUploadFile, index);
            imageFile.src = "/images/file.png";
            imageFile.alt = "file";
            imageFile.className = "imageFile";
            nameFile.className = "nameFile";
            nameFile.textContent = file.name;
            fileContainer.appendChild(deleteFile);
            fileContainer.appendChild(imageFile);
            fileContainer.appendChild(nameFile);
            previousUploadFile.appendChild(fileContainer);
            const fileName = nameFile.textContent;
            const fileNameLength = fileName.length;
            let fileContainerRight;
            let nameFileRight;
            for (let i = 3; i < fileNameLength; i++) {
                fileContainerRight = fileContainer.getBoundingClientRect().right;
                nameFileRight = nameFile.getBoundingClientRect().right;
                if (nameFileRight < fileContainerRight) {
                    break;
                }
                nameFile.textContent = fileName.substring(0, fileNameLength / 2 - i) + ".." + fileName.substring(fileNameLength / 2 + i, fileNameLength);
            }
        } else {
            document.getElementById("errorFile").textContent = document.getElementById("textTooMany").textContent;
            e.value = "";
        }
    }
}

function deleteFiles(e, index) {
    e.remove();
    for (let i = index + 1; i <= size + 1; i++) {
        let uploadFile = document.getElementById("file" + i);
        uploadFile.id = "file" + (i - 1);
        let deleteFile = uploadFile.getElementsByClassName("deleteFile")[0];
        if (deleteFile != null) {
            deleteFile.onclick = () => deleteFiles(uploadFile, i - 1);
        }
    }
    size--;
    if (size === 0) {
        check(1, false, ok, previous, buttonId, success, false, true);
    }
}
