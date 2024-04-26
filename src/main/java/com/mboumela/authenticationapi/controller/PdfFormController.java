package com.mboumela.authenticationapi.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.LoginDto;
import com.mboumela.authenticationapi.dtos.StudentFormDto;
import com.mboumela.authenticationapi.filters.AppUserAuthenticationProvider;
import com.mboumela.authenticationapi.services.AccountService;
import com.mboumela.authenticationapi.services.PdfFormGenerator;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PdfFormController {
	private final PdfFormGenerator pdfFormGenerator;
	private final AppUserAuthenticationProvider appUserAuthenticationProvider;
	
	@PostMapping("/public/login2")
	public ResponseEntity<String> login(@RequestBody @Valid LoginDto loginDto) {
		System.out.println(loginDto);
		return ResponseEntity.ok("ok");
	}

//    @PostMapping("/public/generate-student-pdf")
//    public ResponseEntity<Resource> generatePdfForm(@RequestBody @Valid StudentFormDto studentFormDto) {
//    	System.out.println(studentFormDto);
//        String filePath = "forms/students/"+studentFormDto.nom()+".pdf";
//        pdfFormGenerator.generateStudentForm(filePath, studentFormDto);
//        
//        File file = new File(filePath);
//        ByteArrayResource resource;
//        try {
//            resource = new ByteArrayResource(Files.readAllBytes(file.toPath()));
//        } catch (IOException e) {
//            // Gérer l'erreur de lecture de fichier
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
//                .contentType(MediaType.APPLICATION_PDF)
//                .contentLength(file.length())
//                .body(resource);
//    }
    
    @GetMapping("/public/open-document")
    public ResponseEntity<byte[]> openDocument() throws IOException {
        // Charger le document à partir des ressources
        ClassPathResource resource = new ClassPathResource("form.pdf");

        // Lire le contenu du fichier
        byte[] documentBytes = Files.readAllBytes(Path.of(resource.getURI()));

        // Configurer les en-têtes de la réponse
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", "form.pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Renvoyer le contenu du fichier en tant que réponse
        return ResponseEntity.ok()
                .headers(headers)
                .body(documentBytes);
    }
    
  @GetMapping("/public/open-remote-pdf")
  public ResponseEntity<byte[]> openRemoteFile() throws IOException {
	    String fileUrl = "https://cv-portfolio-elton.vercel.app/static/media/CV_MBOUMELA_Elton.9f1eefc5ea1fc5e7244f.pdf"; // URL du fichier distant
	    String fileName = "CV_MBOUMELA_Elton.9f1eefc5ea1fc5e7244f.pdf"; // Nom de fichier souhaité lors du téléchargement

	    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
	        HttpGet httpGet = new HttpGet(fileUrl);
	        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
	            HttpEntity entity = response.getEntity();
	            byte[] fileBytes = EntityUtils.toByteArray(entity);

	            // Renvoyer le contenu du fichier en tant que réponse
	            return ResponseEntity.ok()
	                    .header("Content-Disposition", "attachment; filename=" + fileName)
	                    .body(fileBytes);
	        }
	    }
	}
    
//  @GetMapping("/public/open-local-file")
//  public ResponseEntity<byte[]> openLocalFile() throws IOException {
//      String filePath = "C:/Users/User/Documents/workspace-spring-tool-suite-4-4.18.0.RELEASE/authentification-api/form.pdf"; // Chemin d'accès complet du fichier local
//
//      File file = new File(filePath);
//      if (!file.exists()) {
//          // Gérer le cas où le fichier n'existe pas
//          return ResponseEntity.notFound().build();
//      }
//
//      byte[] fileBytes = Files.readAllBytes(Path.of(filePath));
//
//      // Renvoyer le contenu du fichier en tant que réponse
//      return ResponseEntity.ok()
//              .header("Content-Disposition", "attachment; filename=" + file.getName())
//              .body(fileBytes);
//  }
}