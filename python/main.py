import sys
import os
from pytube import YouTube

def download_video_or_audio(video_url, output_format):

    yt = YouTube(video_url)

    if output_format == "mp4":

        video_stream = yt.streams.get_highest_resolution()
        downloaded_file_path = video_stream.download(output_path="./downloads")

    elif output_format == "mp3":
        audio_stream = yt.streams.filter(only_audio=True).first()
        downloaded_file_path = audio_stream.download(output_path="./downloads")

    else:
        return None

    if downloaded_file_path:
        with open(downloaded_file_path, 'rb') as file:
            contentBytes = file.read()
        # Remove the downloaded file after reading its bytes
        os.remove(downloaded_file_path)
        title = yt.title
        # Convert strings to bytes and concatenate with video_bytes
        contentBytesArray = [title.encode('utf-8'), b'#', contentBytes]
        return contentBytesArray

    return None

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("Usage: python main.py <video_url> <output_format>")
    else:
        video_url = sys.argv[1]
        output_format = sys.argv[2]
        contentBytesArray = download_video_or_audio(video_url, output_format)

        if contentBytesArray:
            for byte in contentBytesArray:
                sys.stdout.buffer.write(byte)
