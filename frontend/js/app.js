console.log("Diggaren frontend startad");

// Test så vi ser att JavaScript fungerar
const titel = document.querySelector("h1");
titel.style.color = "green";
// koden är inte testad
const baseURL= "http://localhost:7070";
document.addEventListener("DOMContentLoaded",main);

function main(){
    const chanelSelect= document.querySelector("#kanal")
    updateSong(chanelSelect.value);
    chanelSelect.addEventListener("change", ()=>{
        updateSong(chanelSelect.value)
    })
}

async function retriveSong(selectedChanel) {
    const respons= await fetch(`${baseURL}/track/previous/${selectedChanel}`);
    return await respons.json();
}

async function updateSong(selectedChanel){
    const songInfo=  await retriveSong(selectedChanel);
    let song= document.querySelector("#song");
    song.innerHTML= songInfo.title;
    let artist= document.querySelector("#artist");
    artist.innerHTML=songInfo.artist;
    let time= document.querySelector("#time");
    time.innerHTML=songInfo.playedAt;
    updateSpotifyLink(songInfo.spotifyLink);
}

function updateSpotifyLink(spotifyURL){
    let spotifyLink= document.querySelector(".spotify-lank");
    spotifyLink.href=spotifyURL;
}
