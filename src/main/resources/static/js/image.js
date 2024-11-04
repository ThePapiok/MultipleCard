function setImage(e, type, index, isEdit) {
    let upload = document.getElementById("noImage");
    let image = e.files[0];
    let message;
    let error = false;
    const reader = new FileReader();
    const imagePreview = document.getElementById('image');
    if (isEdit) {
        previous[index - 1] = false;
    }
    if (image == null) {
        error = true;
        message = "";
    } else if (!image.type.startsWith("image")) {
        error = true;
        message = document.getElementById("textBadType").textContent;
    } else if (image.size >= 2000000) {
        error = true;
        message = document.getElementById("textTooBig").textContent;
    }
    if (error) {
        imagePreview.src = "/images/nothing.png";
        upload.hidden = false;
        document.getElementById("errorFile").textContent = message;
        e.value = "";
        if (type) {
            previous[4] = true;
            check(5, false, ok, previous, buttonId, success, false, true);
        } else {
            check(index, false, ok, previous, buttonId, success, false, true);
        }
        return;
    }
    reader.readAsDataURL(image);
    reader.onload = function (file) {
        let img = new Image();
        img.src = file.target.result;
        img.onload = function () {
            if (this.width < 450 || this.height < 450) {
                imagePreview.src = "/images/nothing.png";
                upload.hidden = false;
                document.getElementById("errorFile").textContent = document.getElementById("textTooSmall").textContent;
                e.value = "";
                if (type) {
                    previous[4] = true;
                    check(5, false, ok, previous, buttonId, success, false, true);
                } else {
                    check(index, false, ok, previous, buttonId, success, false, true);
                }
            } else {
                imagePreview.src = img.src;
                upload.hidden = true;
                document.getElementById("errorFile").textContent = "";
                if (type) {
                    previous[4] = false;
                    check(5, true, ok, previous, buttonId, success, false, true);
                } else {
                    check(index, true, ok, previous, buttonId, success, false, true);
                }
            }
        }
    };
}
