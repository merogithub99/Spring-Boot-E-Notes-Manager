package com.becoder.controller;

import com.becoder.entity.Notes;
import com.becoder.entity.User;
import com.becoder.repository.NotesRepository;
import com.becoder.repository.UserRepository;
import com.becoder.service.NotesService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotesService notesService;

    @Autowired
    private NotesRepository notesRepository;

    @ModelAttribute
    public User getUser(Principal p, Model m) {
        String email = p.getName();
        User user = userRepo.findByEmail(email);
        m.addAttribute("user", user);
        return user;
    }

    @GetMapping("/addNotes")
    public String addNotes() {

        return "add_notes";
    }

    @GetMapping("/viewNotes")
    public String viewNotes(Model m, Principal p) {
        User user = getUser(p, m);
        notesService.getNotesByUser(user);
        List<Notes> notes = notesService.getNotesByUser(user);
        m.addAttribute("notesList", notes);
        return "view_notes";
    }





    @GetMapping("/editNotes/{id}")
    public String editNotes(@PathVariable int id, Model m) {
        Notes notes = notesService.getNotesById(id);
        m.addAttribute("n", notes);
        return "edit_notes";
    }

    @PostMapping("/saveNotes")
    public String saveNotes(@ModelAttribute Notes notes, HttpSession session, Principal p, Model m) {
        notes.setDate(LocalDate.now());
        notes.setUser(getUser(p, m));
        Notes saveNotes = notesService.saveNotes(notes);
        if (saveNotes != null) {
            session.setAttribute("msg", "Notes save success");
        } else {
            session.setAttribute("msg", "Something wrong on server");
        }

        return "redirect:/user/addNotes";
    }


    @PostMapping("/updateNotes")
    public String updateNotes(@ModelAttribute Notes notes, HttpSession session, Principal p, Model m) {
        notes.setDate(LocalDate.now());
        notes.setUser(getUser(p, m));
        Notes saveNotes = notesService.saveNotes(notes);
        if (saveNotes != null) {
            session.setAttribute("msg", "Notes updated success");
        } else {
            session.setAttribute("msg", "Something wrong on server");
        }

        return "redirect:/user/viewNotes";
    }


    @GetMapping("/deleteNotes/{id}")
    public String deleteNotes(@PathVariable int id, HttpSession session) {
        boolean f = notesService.deleteNotes(id);
        if (f) {
            session.setAttribute("msg", "Notes deleted successfully");
        } else {
            session.setAttribute("msg", "Something wrong on server");
        }
        return "redirect:/user/viewNotes";
    }

    @GetMapping("/viewNotesByCategory")
    public String viewNotesByCategory(@RequestParam String category, Model model, Principal principal) {
        User user = getUser(principal, model);
        List<Notes> notes = notesService.getNotesByCategoryAndUser(category, user);
        List<String> categories = notesService.getAllCategories(); // Assuming you have a method to get all categories
        model.addAttribute("notesList", notes);
        model.addAttribute("categories", categories);
        return "view_notes";
    }

    @GetMapping("/search")
    public String searchNotes(@RequestParam("query") String query, Model model, Principal principal) {
        User user = getUser(principal, model);
        System.out.println(user);

        // Perform the search operation using the notesService
        List<Notes> searchResults = notesService.searchNotes(query, user);
        System.out.println(searchResults);


        // Add the search results to the model attribute
        model.addAttribute("searchResults", searchResults);


        System.out.println(searchResults);
        // Return the view page where you want to display the search results
        return "view_notes";

    }

    @GetMapping("/downloadFile/{noteId}")
    @ResponseBody
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable int noteId) {
        // Retrieve the note by its ID from the repository
        Optional<Notes> optionalNotes = notesRepository.findById(noteId);
        if (optionalNotes.isPresent()) {
            Notes note = optionalNotes.get();
            // Get the file data from the note
            byte[] fileData = note.getFile();

            // Create a ByteArrayResource to wrap the file data
            ByteArrayResource resource = new ByteArrayResource(fileData);

            // Return a ResponseEntity with the file data as a resource
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF) // Set the content type to PDF
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + note.getTitle() + ".pdf\"") // Set the filename for download
                    .body(resource);
        } else {
            // If the note with the given ID is not found, return a 404 Not Found response
            return ResponseEntity.notFound().build();
        }
    }




    @GetMapping("/openFile/{noteId}")
    public String openFile(@PathVariable int noteId, HttpServletResponse response, Principal principal) throws IOException {
        // Retrieve the current user
        User currentUser = userRepo.findByEmail(principal.getName());
        System.out.println(noteId);


        // Retrieve the note by its ID from the repository
        Optional<Notes> optionalNotes = notesRepository.findById(noteId);
        if (optionalNotes.isPresent()) {
            Notes note = optionalNotes.get();


            // Check if the note belongs to the current user
            if (note.getUser().equals(currentUser)) {
                // Get the file data from the note
                byte[] fileData = note.getFile();

                // Set the response content type to PDF
                response.setContentType("application/pdf");

                // Write the file data to the response output stream
                response.getOutputStream().write(fileData);
                response.getOutputStream().flush();

                return null; // To indicate that the response has been handled directly
            } else {
                // If the note does not belong to the current user, return a 403 Forbidden response
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return null; // To indicate that the response has been handled directly
            }
        } else {
            // If the note with the given ID is not found, return a 404 Not Found response
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return null; // To indicate that the response has been handled directly
        }
    }





}





