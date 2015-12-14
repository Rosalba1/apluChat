
var tema = 'General';
var evtSource = new EventSource("ChatServlet");
evtSource.onmessage = function(e){
    var data = JSON.parse(e.data);
    console.log(e);
    console.log(data);
    if(tema==data.tema){
        var received = data.user + ":"+data.text +"\n\n";
        document.getElementById('contedido').value = document.getElementById('contedido').value + received;
    }
};
evtSource.addEventListener('nuevoCanal', function(e){
    var data = JSON.parse(e.data);
    console.log(e);
    console.log(data);
    
    var nuevoCont = document.createElement('div');
    var nomCont = document.createElement('h3');
    nomCont.appendChild(document.createTextNode(data.nomGrup));
    nuevoCont.setAttribute('class', 'grupos');
    nuevoCont.appendChild(nomCont);
    nuevoCont.setAttribute('id',data.nomGrup);
    nuevoCont.setAttribute('onclick',"tema=\""+data.nomGrup+"\"; limpiar();");
   
    
    var contAnterior = document.getElementsByClassName('contGrupos')[0].getElementsByClassName('grupos')[0];
    document.getElementsByClassName('contGrupos')[0].insertBefore(nuevoCont,contAnterior);
});
function limpiar(){
    document.getElementById('contedido').value='';
}

var nomGrupo = document.getElementById("btnNuevoGrupo");
nomGrupo.addEventListener('click', function (){
    if(document.getElementById(document.getElementById('nuevoGrupo').value)==null){
        var request  = new XMLHttpRequest();

        request.addEventListener('load',function (){
             console.log('Message sent!');
        });

       request.open('POST','ChatServlet',true);
       request.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset=utf-8");
       request.send("nomGrup="+document.getElementById('nuevoGrupo').value+"&evento=nuevoGrupo");


       document.getElementById('nuevoGrupo').value = '';
    }else{
        alert('El nombre del canal ya existe..\nIntenta con un nuevo nombre.');
    }
});

var message = document.getElementById("enviar");
message.addEventListener('click', function (){
    var request  = new XMLHttpRequest();

    request.addEventListener('load',function (){
         console.log('Message sent!');
    });
   request.open('POST','ChatServlet',true);
   request.setRequestHeader("Content-type","application/x-www-form-urlencoded; charset=utf-8");
   request.send("user="+document.getElementById('usuario').value+"&text="+document.getElementById('texto').value+"&tema="+tema);
   document.getElementById('texto').value = '';
});

evtSource.addEventListener('eliminarCanal', function(e){
    var data = JSON.parse(e.data);
    console.log(e);
        console.log(data);
    document.getElementById(data.quitarGrup).parentNode.removeChild(document.getElementById(data.quitarGrup));
   
});
