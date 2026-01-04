console.log("Diggaren frontend startad");

const baseURL= "http://localhost:7070";
document.addEventListener("DOMContentLoaded",main);

function main(){
    const chanelSelect= document.querySelector("#kanal")
    const kanalKort = document.querySelectorAll(".kanal-kort");
    const indicators = document.querySelectorAll(".indicator");
    const carousel = document.querySelector("#kanal-carousel");
    
    updateSong(chanelSelect.value);
    
    kanalKort.forEach((kort, index) => {
        kort.addEventListener("click", () => {
            const kanalValue = kort.getAttribute("data-kanal");
            
            kanalKort.forEach(k => k.classList.remove("active"));
            kort.classList.add("active");
            
            chanelSelect.value = kanalValue;
            
            indicators.forEach(i => i.classList.remove("active"));
            indicators[index].classList.add("active");
            
            kort.scrollIntoView({ behavior: "smooth", block: "nearest", inline: "center" });
            
            updateSong(kanalValue);
        });
    });
    
    let touchStartX = 0;
    let touchEndX = 0;
    
    carousel.addEventListener("touchstart", (e) => {
        touchStartX = e.changedTouches[0].screenX;
    });
    
    carousel.addEventListener("touchend", (e) => {
        touchEndX = e.changedTouches[0].screenX;
        handleSwipe();
    });
    
    function handleSwipe() {
        const swipeThreshold = 50;
        const diff = touchStartX - touchEndX;
        
        if (Math.abs(diff) > swipeThreshold) {
            const activeKort = document.querySelector(".kanal-kort.active");
            const activeIndex = Array.from(kanalKort).indexOf(activeKort);
            
            if (diff > 0 && activeIndex < kanalKort.length - 1) {
                kanalKort[activeIndex + 1].click();
            } else if (diff < 0 && activeIndex > 0) {
                kanalKort[activeIndex - 1].click();
            }
        }
    }
    
    document.addEventListener("keydown", (e) => {
        const activeKort = document.querySelector(".kanal-kort.active");
        const activeIndex = Array.from(kanalKort).indexOf(activeKort);
        
        if (e.key === "ArrowLeft" && activeIndex > 0) {
            kanalKort[activeIndex - 1].click();
        } else if (e.key === "ArrowRight" && activeIndex < kanalKort.length - 1) {
            kanalKort[activeIndex + 1].click();
        }
    });
    
    indicators.forEach((indicator, index) => {
        indicator.addEventListener("click", () => {
            kanalKort[index].click();
        });
    });
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
    if(songInfo.playedAt){
        const dataObject= new Date(songInfo.playedAt*1000);
        const formattedTime= dataObject.toLocaleTimeString('sv-SE',{
            hour:'2-digit',
            minute: '2-digit'
        })
        time.innerHTML=formattedTime;
    }else{
        time.innerHTML="--:--"
    }
    updateSpotifyLink(songInfo.spotifyLink);
}

function updateSpotifyLink(spotifyURL){
    let spotifyLink= document.querySelector(".spotify-lank");
    spotifyLink.href=spotifyURL;
}
