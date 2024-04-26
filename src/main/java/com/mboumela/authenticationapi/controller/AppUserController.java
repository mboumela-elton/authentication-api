package com.mboumela.authenticationapi.controller;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mboumela.authenticationapi.AuthenticationApi;
import com.mboumela.authenticationapi.dtos.AppUserDto;
import com.mboumela.authenticationapi.dtos.LoginDto;
import com.mboumela.authenticationapi.dtos.SignUpDto;
import com.mboumela.authenticationapi.dtos.StudentFormDto;
import com.mboumela.authenticationapi.dtos.WorkerFormDto;
import com.mboumela.authenticationapi.entities.AppUser;
import com.mboumela.authenticationapi.exceptions.ApplicationException;
import com.mboumela.authenticationapi.filters.AppUserAuthenticationProvider;
import com.mboumela.authenticationapi.services.AccountService;
import com.mboumela.authenticationapi.services.PdfFormGenerator;
import com.mboumela.authenticationapi.utils.RolesEnum;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class AppUserController {

	private final AccountService accountService;
	private final AppUserAuthenticationProvider appUserAuthenticationProvider;
	private final PdfFormGenerator pdfFormGenerator;

//	@Value("${parent.path}")
//	private String parentPath;
	private String parentPath = AuthenticationApi.parentFolder;
	
	@Value("${forms.students.folder}")
	private String formsStudentsFolder;

	@Value("${forms.workers.folder}")
	private String formsWorkersFolder;
	
	@Value("${cv.workers.folder}")
	private String cvWorkersFolder;

	@Value("${cv.students.folder}")
	private String cvStudentsFolder;

	private String pdf = ".pdf";
	
	// user controller

	@Operation(summary = "to sign up user")
	@PostMapping("/public/signup")
	public ResponseEntity<AppUserDto> Signup(@RequestBody @Valid SignUpDto signUpDto) {
		AppUserDto newUser = accountService.addNewUser(signUpDto);
		newUser = accountService.addRoleToUser(newUser.getEmail(), RolesEnum.USER.name());
		newUser.setToken(appUserAuthenticationProvider.createToken(newUser));
		return ResponseEntity.created(URI.create("/user/" + newUser.getEmail())).body(newUser);
	}

	@Operation(summary = "to login user")
	@PostMapping("/public/login")
	public ResponseEntity<AppUserDto> login(@RequestBody @Valid LoginDto loginDto) {
		AppUserDto appUserDto = accountService.loginVerification(loginDto);
		appUserDto.setToken(appUserAuthenticationProvider.createToken(appUserDto));
		return ResponseEntity.ok(appUserDto);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/get-all-users")
	public ResponseEntity<List<AppUser>> allusers() {
		List<AppUser> users = accountService.findAllUsers();
		return ResponseEntity.ok(users);
	}

	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/delete-user")
	public ResponseEntity<String> deleteUser(@RequestParam("userId") Long userId) {
		accountService.deleteAppUser(userId);
		return ResponseEntity.ok("user id :'" + userId + "' is deleted");
	}

	// file controller

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping("/generate-student-pdf")
	public ResponseEntity<String> generateStudentPdfForm(@RequestBody @Valid StudentFormDto studentFormDto) {
		String filePath = parentPath + formsStudentsFolder;
		pdfFormGenerator.generateStudentForm(filePath, studentFormDto);

		return ResponseEntity.ok("yo");
	}

	@PreAuthorize("hasRole('ROLE_USER')")
	@PostMapping("/generate-worker-pdf")
	public ResponseEntity<String> generateWorkerPdfForm(@RequestBody @Valid WorkerFormDto workerFormDto) {
		String filePath = parentPath + formsWorkersFolder;
		pdfFormGenerator.generateWorkerForm(filePath, workerFormDto);

		return ResponseEntity.ok("yo");
	}

	@GetMapping("/open-local-file")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<byte[]> openLocalFile(@RequestParam("userId") int userId) throws IOException {
		AppUser user = accountService.getuserByUserId(userId);

		if (!user.getForms().isEmpty()) {
			String filePath = user.getForms().get(0).getFilePath();
			String path = filePath;

			File file = new File(path);
			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}

			byte[] fileBytes = Files.readAllBytes(Path.of(filePath));

			return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + file.getName())
					.body(fileBytes);

		} else {
			throw new ApplicationException("The form to download is not yet available, you must complete it!",
					HttpStatus.NOT_FOUND);
		}

	}
	
	@GetMapping("/open-local-cv")
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
	public ResponseEntity<byte[]> openLocalFile2(@RequestParam("userId") int userId) throws IOException {
		AppUser user = accountService.getuserByUserId(userId);

		if (!user.getCvs().isEmpty()) {
			String filePath = user.getCvs().get(0).getFilePath();
			String path = filePath;

			File file = new File(path);
			if (!file.exists()) {
				return ResponseEntity.notFound().build();
			}

			byte[] fileBytes = Files.readAllBytes(Path.of(filePath));

			return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=" + file.getName())
					.body(fileBytes);

		} else {
			throw new ApplicationException("The form to download is not yet available, you must complete it!",
					HttpStatus.NOT_FOUND);
		}

	}
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/upload-student-cv")
    public void handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("userId") Long userId) {
        pdfFormGenerator.fileUpload(file, parentPath, cvStudentsFolder, userId);
    }
	
	@PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
    @PostMapping("/upload-worker-cv")
    public void handleFileUpload2(@RequestPart("file") MultipartFile file, @RequestParam("userId") Long userId) {
        pdfFormGenerator.fileUpload(file, parentPath, cvWorkersFolder, userId);
    }
}
