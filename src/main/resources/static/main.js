
$(document).ready(function () {
    Main();
});

function Main() {

    var selectedFormat = "";
    var alreadyProcessing = false;

    $('.mp3Box').click(function () {
        selectedFormat = "mp3";
        $('.mp3Box').addClass('selected');
        $('.mp4Box').removeClass('selected');
    });

    $('.mp4Box').click(function () {
        selectedFormat = "mp4";
        $('.mp4Box').addClass('selected');
        $('.mp3Box').removeClass('selected');
    });

    $('.downloadBox').click(function (event) {

        if (selectedFormat !== "") {

            if (!alreadyProcessing) {

                console.log("Downloading video!");
                alreadyProcessing = true;

                // Add the "active" class to the principalBox to change the box-shadow color smoothly
                $('.principalBox').removeClass('error').addClass('active');

                $.ajax({
                    url: 'http://localhost:8080/request/url',
                    type: 'POST',
                    data: JSON.stringify({
                        url: $('#userInput').val(),
                        fileFormat: selectedFormat
                    }),
                    async: true,
                    contentType: 'application/json',
                    success: function (data) {

                        const musicName = data.musicName;
                        const musicBytesBase64 = data.musicBytes;

                        // Decode the Base64 string into a Uint8Array
                        const musicBytes = Uint8Array.from(atob(musicBytesBase64), (c) => c.charCodeAt(0));

                        // Create a Blob from the Uint8Array
                        const blob = new Blob([musicBytes], { type: 'application/octet-stream' });

                        // Initiate a download
                        const url = window.URL.createObjectURL(blob);
                        const a = document.createElement('a');
                        a.style.display = 'none';
                        document.body.appendChild(a);
                        a.href = url;
                        a.download = musicName + '.' + selectedFormat;
                        a.click();
                        window.URL.revokeObjectURL(url);
                        document.body.removeChild(a);

                        alreadyProcessing = false;
                    },
                    error: function (error) {

                        alreadyProcessing = false;
                        $('.principalBox').addClass('error');
                        alert("Insert a valid youtube URL");
                        console.error('Error:', error);
                    }
                });

            }
            else{
                alert("Video already downloading, please wait..");
            }
        }
        else {
            alert("Select a valid download format");
        }
    });

}




