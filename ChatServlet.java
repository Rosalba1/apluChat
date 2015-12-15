package paquete;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ChatServlet", urlPatterns = {"/ChatServlet"})
public class ChatServlet extends HttpServlet {
    private HashMap<HttpServletResponse, HttpServletResponse> clients = new HashMap<>();
    private HashMap<String, String> canal = new HashMap<>();
     private HashMap<String, Integer> ctrlCanal = new HashMap<>();
    private HashMap<HttpServletResponse, Integer> tiempoInactivoClientes = new HashMap<>();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            response.setContentType("text/event-stream;charset=utf-8");
            clients.put(response,response);
            tiempoInactivoClientes.put(response, 0);
            enviarCanal(response);
            while(true){
                try{
//                    if(tiempoInactivoClientes.get(response)==12){
//                        clients.remove(response);
//                        break;
//                    }
                    Thread.sleep(5000);
                    //tiempoInactivoClientes.put(response,tiempoInactivoClientes.get(response)+1);
                }catch(Exception e){
                    
                }
            }
    }
    public  void enviarCanal(HttpServletResponse d) throws IOException{
        for(String nomGrup: canal.values()){
            d.getWriter().write("event: nuevoCanal\n");
            d.getWriter().write("data: {\"type\":\"message\","+"\"nomGrup\":\""+nomGrup+"\"}\n\n");
            d.getWriter().flush();
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String text = request.getParameter("text");
        String user = request.getParameter("user");
        String nomGrup = request.getParameter("nomGrup");
        String tema = request.getParameter("tema");
        tiempoInactivoClientes.put(response, 0);
        
        String evento = request.getParameter("evento");
        if(nomGrup!=null){
            for(HttpServletResponse c: clients.values()){
                if(evento==null){
                    c.getWriter().write("data: {\"type\":\"message\","+"\"user\":\""+user+"\", "+"\"text\":\""+text+"\", "+"\"tema\":\""+tema+"\"}\n\n");            
                    c.getWriter().flush();
                } else{
                    c.getWriter().write("event: nuevoCanal\n");
                    c.getWriter().write("data: {\"type\":\"message\","+"\"nomGrup\":\""+nomGrup+"\"}\n\n");
                    c.getWriter().flush();
                }
            }
            canal.put(nomGrup,nomGrup);
            ctrlCanal.put(nomGrup, 0);
            monCanales(nomGrup);
        }else{
            ctrlCanal.put(tema, 0);
        }
        
        for(HttpServletResponse c: clients.values()){
            if(evento==null){
                c.getWriter().write("data: {\"type\":\"message\","+"\"user\":\""+user+"\", "+"\"text\":\""+text+"\", "+"\"tema\":\""+tema+"\"}\n\n");            
                c.getWriter().flush();
            } 
        }
        
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
    
     protected void monCanales(String nom){
            while(true){
                try{
                    if(ctrlCanal.get(nom)>=12){
                        canal.remove(nom);
                        ctrlCanal.remove(nom);
                         for(HttpServletResponse c: clients.values()){
                            c.getWriter().write("event: eliminarCanal\n");
                            c.getWriter().write("data: {\"type\":\"message\","+"\"quitarGrup\":\""+nom+"\"}\n\n");
                            c.getWriter().flush();
                        }
                         break;
                    }
                    System.out.println(nom+" tiempo: "+ctrlCanal.get(nom)+"cant Canales: "+ctrlCanal.size());
                    Thread.sleep(5000);
                    ctrlCanal.put(nom, ctrlCanal.get(nom)+1);
                }catch(Exception e){
                    
                }
            }
    }

}
