package com.becoder.controller;

import com.becoder.entity.Notes;
import com.becoder.entity.User;
import com.becoder.repository.NotesRepository;
import com.becoder.repository.UserRepository;
import com.becoder.service.NotesService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private NotesService notesService;

    @Autowired
    private NotesRepository notesRepository;

    @Value("classpath:/static/img/")
    private String uploadDir;

//    @PostMapping("/saveNotes")
//    public String saveNotes(@ModelAttribute Notes notes,
//                            @RequestParam("image") MultipartFile image,
//                            HttpSession session, Principal p, Model m) {
//
//        if (image.isEmpty()) {
//            // Handle empty file error
//            session.setAttribute("msg", "Please select a file to upload");
//            return "redirect:/user/addNotes";
//        }
//
//        try {
//            // Generate a unique filename for the uploaded image
//            String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
//
//            // Get the username or user ID of the logged-in user
//            String username = p.getName(); // Assuming the username is used as the user identifier
//            System.out.println(username);
//
//            // Construct the directory path for the user's images
//            String userDirPath = uploadDir + File.separator + username;
//
//            // Create the user's directory if it doesn't exist
//            File userDir = new File(userDirPath);
//            if (!userDir.exists()) {
//                userDir.mkdirs(); // Create the directory and any necessary parent directories
//            }
//
//            // Save the image to the file system
//            String filePath = userDirPath + File.separator + fileName;
//            System.out.println(filePath);
//            image.transferTo(new File(filePath));
//
//            // Set the file path in the notes object
//            notes.setImage("/img/" + username + "/" + fileName);
//
//            // Set other attributes of the notes object
//            notes.setDate(LocalDate.now());
//            notes.setUser(getUser(p, m));
//
//            // Save the notes object to the database
//            Notes saveNotes = notesService.saveNotes(notes);
//
//            if (saveNotes != null) {
//                session.setAttribute("msg", "Notes saved successfully");
//            } else {
//                session.setAttribute("msg", "Something went wrong while saving notes");
//            }
//
//            return "redirect:/user/addNotes";
//        } catch (IOException e) {
//            e.printStackTrace();
//            // Handle file processing error
//            session.setAttribute("msg", "Error occurred while uploading the file");
//            return "redirect:/user/addNotes";
//        }
//    }


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
        List<Notes> notes = notesService.getNotesByUser(user);
        int totalNotesCount = notes.size(); // Count the notes retrieved for the user

        // Add the totalNotesCount to the model
        m.addAttribute("totalNotesCount", totalNotesCount);
        m.addAttribute("notesList", notes);
        System.out.println(totalNotesCount);
        return "view_notes";
    }






    @GetMapping("/editNotes/{id}")
    public String editNotes(@PathVariable int id, Model m) {
        Notes notes = notesService.getNotesById(id);
        m.addAttribute("n", notes);
        return "edit_notes";
    }

    @PostMapping("/saveNotes")
    public String saveNotes(@ModelAttribute Notes notes,@RequestParam MultipartFile img, HttpSession session, Principal p, Model m,User user) {
        notes.setDate(LocalDate.now());

        String imgName=img.getOriginalFilename();
        notes.setImage(img.getOriginalFilename());
        notes.setUser(getUser(p, m));
        Notes saveNotes = notesService.saveNotes(notes);
        if (saveNotes != null) {
            try {
               File savefile=  new ClassPathResource("static/img").getFile();


               Path path= Paths.get(savefile.getAbsolutePath()+ File.separator+img.getOriginalFilename());
                System.out.println(path);
                Files.copy(img.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);

            }catch (Exception e){
                e.printStackTrace();

            }

            session.setAttribute("msg", "Notes save success");
        } else {
            session.setAttribute("msg", "Something wrong on server");
        }

        return "redirect:/user/addNotes";
    }


//    @PostMapping("/updateNotes")
//    public String updateNotes(@ModelAttribute Notes notes, HttpSession session, Principal p, Model m) {
//        notes.setDate(LocalDate.now());
//        notes.setUser(getUser(p, m));
//        Notes saveNotes = notesService.saveNotes(notes);
//        if (saveNotes != null) {
//            session.setAttribute("msg", "Notes updated success");
//        } else {
//            session.setAttribute("msg", "Something wrong on server");
//        }
//
//        return "redirect:/user/viewNotes";
//    }


    @PostMapping("/updateNotes")
    public String updateNotes(@ModelAttribute Notes notes, @RequestParam MultipartFile img, HttpSession session, Principal p, Model m) {
        // Get the current date for the updated note
        notes.setDate(LocalDate.now());
        // Get the user associated with the note
        notes.setUser(getUser(p, m));

        // Check if a new image is uploaded
        if (!img.isEmpty()) {
            try {
                // Save the new image to the file system
                File savefile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + img.getOriginalFilename());
                Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Update the image path in the note object
                notes.setImage(img.getOriginalFilename());
            } catch (Exception e) {
                e.printStackTrace();
                session.setAttribute("msg", "Failed to update image");
                return "redirect:/user/viewNotes";
            }
        }


        // Save the updated note
        Notes saveNotes = notesService.saveNotes(notes);
        if (saveNotes != null) {
            session.setAttribute("msg", "Notes updated successfully");
        } else {
            session.setAttribute("msg", "Failed to update notes");
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
        // Perform the search operation using the notesService
        List<Notes> searchResults = notesService.searchNotes(query, user);
        System.out.println(searchResults);
        // Add the search results to the model attribute
        model.addAttribute("searchResults", searchResults);
        System.out.println(searchResults);
        // Return the view page where you want to display the search results
        return "view_notes";

    }


}





