package com.mboumela.authenticationapi.services;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.StudentFormDto;
import com.mboumela.authenticationapi.dtos.WorkerFormDto;
import com.mboumela.authenticationapi.entities.AppRole;
import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.entities.Cv;
import com.mboumela.authenticationapi.entities.Form;
import com.mboumela.authenticationapi.exceptions.ApplicationException;
import com.mboumela.authenticationapi.repository.AppUserRepository;
import com.mboumela.authenticationapi.repository.CvRepository;
import com.mboumela.authenticationapi.repository.FormRepository;

import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PdfFormGenerator {
	
	private final CvRepository cvRepository;
	private final FormRepository formRepository;
	private final AppUserRepository appUserRepository;
	
	private String pdf = ".pdf";
	private static AppUser appUser;
	private static String fileP;
	
	public void deleteFile(String filePath) {
	    File file = new File(filePath);

	    if (file.exists()) {
	        if (file.delete()) {
	            System.out.println("Fichier supprimé avec succès !");
	        } else {
	            System.err.println("Erreur lors de la suppression du fichier.");
	        }
	    } else {
	        System.err.println("Le fichier n'existe pas.");
	    }
	}
	
	public void addFormToUser(String filePath, Long userId) {
		appUser = appUserRepository.findById(userId).orElseThrow(
				() -> new ApplicationException("user not found", HttpStatus.NOT_FOUND));
		
		if(!appUser.getForms().isEmpty()) {
			deleteFile(appUser.getForms().get(0).getFilePath());
		}
		fileP = filePath + appUser.getUsername() + pdf;
		Form form = Form.builder().filePath(fileP).build();
		formRepository.save(form);
		
		List<Form> forms = new ArrayList<>();
		forms.add(form);
		
		appUser.setForms(forms);
	}
	
	public void addCvToUser(String filePath, Long userId) {
		appUser = appUserRepository.findById(userId).orElseThrow(
				() -> new ApplicationException("user not found", HttpStatus.NOT_FOUND));
		
		if(!appUser.getCvs().isEmpty()) {
			deleteFile(appUser.getCvs().get(0).getFilePath());
		}
		
		fileP = filePath + "CV-" + appUser.getUsername() + pdf;
		Cv cv = Cv.builder().filePath(fileP).build();
		cvRepository.save(cv);
		
		List<Cv> cvs = new ArrayList<>();
		cvs.add(cv);
		
		appUser.setCvs(cvs);
	}
	
	public ResponseEntity<String> fileUpload(MultipartFile file, String parentPath, String cvStudentsFolder, Long userId) {
	    try {
	        if (file.isEmpty()) {
	            return ResponseEntity.badRequest().body("Le fichier est vide");
	        }
	        
	        if (file.getSize() > 1048576) {
	            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body("Le fichier dépasse la taille maximale autorisée (1 Mo)");
	        }

	        String uploadDir = parentPath + cvStudentsFolder;
	        addCvToUser(uploadDir, userId);
	        
	        String originalFilename = appUser.getCvs().get(0).getFilePath();

	        Path uploadPath = Path.of(uploadDir);

	        if (!Files.exists(uploadPath)) {
	            Files.createDirectories(uploadPath);
	        }

	        Path filePath = uploadPath.resolve(originalFilename);
	        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

	        return ResponseEntity.ok("Fichier uploadé avec succès");
	    } catch (IOException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'upload du fichier");
	    }
	}
	
    public void generateStudentForm(String filePath, StudentFormDto studentFormDto) {
    	
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page); 

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Formulaire d'inscription");
                contentStream.endText();

                // Ajouter les informations personnelles
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 655);
                contentStream.showText("Données personnelles:");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 630);
                contentStream.showText("Nom(s): ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.nom());
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 610);
                contentStream.showText("Prénom(s): ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.prenom());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 590);
                contentStream.showText("Date de naissance: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.dateNaissance());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 570);
                contentStream.showText("Lieu de naissance: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.lieuNaissance());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 550);
                contentStream.showText("Adresse: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.adresse());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 530);
                contentStream.showText("Téléphone: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.telephone());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 510);
                contentStream.showText("Email: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.email());
                contentStream.endText();

                // Ajouter les informations académiques
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 465);
                contentStream.showText("Profil académique:");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 440);
                contentStream.showText("Établissement: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.etablissement());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 420);
                contentStream.showText("Filière: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.filiere());
                contentStream.endText();
                
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 400);
                contentStream.showText("Niveau: ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(studentFormDto.niveau());
                contentStream.endText();
                
                // Ajouter la motivation
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 370);
                contentStream.showText("Motivation:");
                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 350);

                String motivation = studentFormDto.motivation();
                int maxWidth = 400; // Specify the maximum width for the text
                int fontSize = 12;

                List<String> lines = new ArrayList<>();
                StringBuilder currentLine = new StringBuilder();

                for (String word : motivation.split("\\s+")) {
                    float wordWidth = PDType1Font.HELVETICA.getStringWidth(word) / 1000 * fontSize;
                    float currentLineWidth = PDType1Font.HELVETICA.getStringWidth(currentLine.toString()) / 1000 * fontSize;

                    if (currentLineWidth + wordWidth > maxWidth) {
                        lines.add(currentLine.toString());
                        currentLine = new StringBuilder(word);
                    } else {
                        if (currentLine.length() > 0) {
                            currentLine.append(" ");
                        }
                        currentLine.append(word);
                    }
                }

                lines.add(currentLine.toString());

                for (String line : lines) {
                    contentStream.showText(line);
                    contentStream.newLineAtOffset(0, -fontSize); // Move to the next line
                }

                contentStream.endText();
            }

            addFormToUser(filePath, studentFormDto.userId());
            document.save(fileP);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
public void generateWorkerForm(String filePath, WorkerFormDto workerFormDto) {
    	
    try (PDDocument document = new PDDocument()) {
        PDPage page = new PDPage();
        document.addPage(page); 

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Formulaire d'inscription");
            contentStream.endText();

            // Ajouter les informations personnelles
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 655);
            contentStream.showText("Informations personnelles:");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 630);
            contentStream.showText("Nom:");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.nom());
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 610);
            contentStream.showText("Prénom:");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.prenom());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 590);
            contentStream.showText("Date de naissance: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.dateNaissance());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 570);
            contentStream.showText("Lieu de naissance: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.lieuNaissance());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 550);
            contentStream.showText("Adresse: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.adresse());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 530);
            contentStream.showText("Téléphone: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.telephone());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 510);
            contentStream.showText("Email: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.email());
            contentStream.endText();

            // Ajouter les informations académiques
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 465);
            contentStream.showText("Informations académiques:");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 440);
            contentStream.showText("Lieu de service: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.lieuService());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 420);
            contentStream.showText("Poste occupé: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.posteOccupe());
            contentStream.endText();
            
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 400);
            contentStream.showText("Expérience: ");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.showText(workerFormDto.experience());
            contentStream.endText();
            
            // Ajouter la motivation
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 370);
            contentStream.showText("Motivation:");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(100, 350);

            String motivation = workerFormDto.motivation();
            int maxWidth = 400; // Specify the maximum width for the text
            int fontSize = 12;

            List<String> lines = new ArrayList<>();
            StringBuilder currentLine = new StringBuilder();

            for (String word : motivation.split("\\s+")) {
                float wordWidth = PDType1Font.HELVETICA.getStringWidth(word) / 1000 * fontSize;
                float currentLineWidth = PDType1Font.HELVETICA.getStringWidth(currentLine.toString()) / 1000 * fontSize;

                if (currentLineWidth + wordWidth > maxWidth) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    if (currentLine.length() > 0) {
                        currentLine.append(" ");
                    }
                    currentLine.append(word);
                }
            }

            lines.add(currentLine.toString());

            for (String line : lines) {
                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -fontSize); // Move to the next line
            }

            contentStream.endText();
        }

        addFormToUser(filePath, workerFormDto.userId());
        document.save(fileP);
        
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}