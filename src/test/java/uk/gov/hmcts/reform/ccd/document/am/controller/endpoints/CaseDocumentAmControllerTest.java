package uk.gov.hmcts.reform.ccd.document.am.controller.endpoints;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.ccd.document.am.model.CaseDocumentMetadata;
import uk.gov.hmcts.reform.ccd.document.am.model.Document;
import uk.gov.hmcts.reform.ccd.document.am.model.StoredDocumentHalResource;
import uk.gov.hmcts.reform.ccd.document.am.model.enums.Permission;
import uk.gov.hmcts.reform.ccd.document.am.service.CaseDataStoreService;
import uk.gov.hmcts.reform.ccd.document.am.service.DocumentManagementService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import static org.mockito.Mockito.*;

@SuppressWarnings("PMD.DataflowAnomalyAnalysis")
public class CaseDocumentAmControllerTest {
    @InjectMocks
    private transient CaseDocumentAmController testee;

    @Mock
    private transient DocumentManagementService documentManagementService;

    @Mock
    private transient CaseDataStoreService caseDataStoreService;

    private transient ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);
    private transient String serviceAuthorization = "";

    private static final String MATCHED_DOCUMENT_ID = "41334a2b-79ce-44eb-9168-2d49a744be9c";
    private static final String UNMATCHED_DOCUMENT_ID = "41334a2b-79ce-44eb-9168-2d49a744be9d";
    private static final String CASE_ID = "1582550122096256";

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetValidMetaDataResponse() {
        doReturn(setDocumentMetaData()).when(documentManagementService).getDocumentMetadata(getUuid());

        ResponseEntity response = testee.getDocumentbyDocumentId(serviceAuthorization, getUuid(), "", "");

        assertAll(
            () ->  assertNotNull(response, "Valid Response from API"),
            () -> assertNotNull(response.getBody(), "Valid response body"),
            () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code is OK")
        );
    }

    @Test
    public void shouldNotGetValidMetaDataResponse() {
        doReturn(responseEntity).when(documentManagementService).getDocumentMetadata(getUuid());

        ResponseEntity response = testee.getDocumentbyDocumentId(serviceAuthorization, getUuid(), "", "");

        assertAll(
            () ->  assertNull(response.getBody(), "No response"),
            () -> assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code")
        );
    }

    @Test
    @DisplayName("should get 200 document binary content")
    public void shouldGetDocumentBinaryContent() {
        CaseDocumentMetadata caseDocumentMetadata = getCaseDocumentMetadata(
            MATCHED_DOCUMENT_ID,
            Arrays.asList(
                Permission.CREATE,
                Permission.READ
            )
        );
        doReturn(setDocumentMetaData()).when(documentManagementService).getDocumentMetadata(getUuid());
        doReturn(CASE_ID).when(documentManagementService).extractCaseIdFromMetadata(setDocumentMetaData().getBody());
        doReturn(caseDocumentMetadata).when(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid());
        doReturn(setDocumentBinaryContent("OK")).when(documentManagementService).getDocumentBinaryContent(getUuid());

        ResponseEntity<Object> response = testee.getDocumentBinaryContentbyDocumentId(
            serviceAuthorization,
            getUuid(),
            "",
            ""
        );

        assertAll(
            () -> verify(documentManagementService).getDocumentMetadata(getUuid()),
            () -> verify(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid()),
            () -> verify(documentManagementService, times(1)).getDocumentBinaryContent(getUuid()),
            () -> assertThat(response.getStatusCode(), is(HttpStatus.OK)),
            () -> assertNotNull(response.getBody())
        );
    }

    @Test
    @DisplayName("should throw 403 forbidden when the requested document does not have read permission")
    public void shouldThrowForbiddenWhenDocumentDoesNotHaveReadPermission() {
        CaseDocumentMetadata caseDocumentMetadata = getCaseDocumentMetadata(
            MATCHED_DOCUMENT_ID,
            Arrays.asList(Permission.CREATE)
        );
        doReturn(setDocumentMetaData()).when(documentManagementService).getDocumentMetadata(getUuid());
        doReturn(CASE_ID).when(documentManagementService).extractCaseIdFromMetadata(setDocumentMetaData().getBody());
        doReturn(caseDocumentMetadata).when(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid());
        doReturn(setDocumentBinaryContent("FORBIDDEN")).when(documentManagementService).getDocumentBinaryContent(getUuid());

        ResponseEntity<Object> response = testee.getDocumentBinaryContentbyDocumentId(
            serviceAuthorization,
            getUuid(),
            "",
            ""
        );

        assertAll(
            () -> verify(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid()),
            () -> assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN)),
            () -> assertNotNull(response.getBody()),
            () -> assertThat(response.getBody(), is("User don't have read permission on requested document"))
        );
    }

    @Test
    @DisplayName("should throw 404 not found when the requested document does not exist")
    public void shouldThrowNotFoundWhenDocumentDoesNotExist() {
        CaseDocumentMetadata caseDocumentMetadata = CaseDocumentMetadata.builder().caseId("").build();
        doReturn(setDocumentMetaData()).when(documentManagementService).getDocumentMetadata(getUuid());
        doReturn(CASE_ID).when(documentManagementService).extractCaseIdFromMetadata(setDocumentMetaData().getBody());
        doReturn(caseDocumentMetadata).when(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid());
        doReturn(setDocumentBinaryContent("FORBIDDEN")).when(documentManagementService).getDocumentBinaryContent(getUuid());

        ResponseEntity<Object> response = testee.getDocumentBinaryContentbyDocumentId(
            serviceAuthorization,
            getUuid(),
            "",
            ""
        );

        assertAll(
            () -> verify(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid()),
            () -> assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND)),
            () -> assertNotNull(response.getBody()),
            () -> assertThat(
                response.getBody(),
                is("Document doesn't exist for requested documetd id at CCD Data Store API Side")
            )
        );
    }

    @Test
    @DisplayName("should throw 403 forbidden when the requested document does not match with available doc")
    public void shouldThrowForbiddenWhenDocumentDoesNotMatch() {
        CaseDocumentMetadata caseDocumentMetadata = getCaseDocumentMetadata(
            UNMATCHED_DOCUMENT_ID,
            Arrays.asList(
                Permission.CREATE,
                Permission.READ
            )
        );
        doReturn(setDocumentMetaData()).when(documentManagementService).getDocumentMetadata(getUuid());
        doReturn(CASE_ID).when(documentManagementService).extractCaseIdFromMetadata(setDocumentMetaData().getBody());
        doReturn(caseDocumentMetadata).when(caseDataStoreService).getCaseDocumentMetadata(CASE_ID, getUuid());
        doReturn(setDocumentBinaryContent("FORBIDDEN")).when(documentManagementService).getDocumentBinaryContent(getUuid());

        ResponseEntity<Object> response = testee.getDocumentBinaryContentbyDocumentId(
            serviceAuthorization,
            getUuid(),
            "",
            ""
        );

        assertAll(
            () -> verify(documentManagementService).getDocumentMetadata(getUuid()),
            () -> assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN)),
            () -> assertNotNull(response.getBody()),
            () -> assertThat(response.getBody(), is("User don't have read permission on requested document"))
        );
    }

    private ResponseEntity<StoredDocumentHalResource> setDocumentMetaData() {
        StoredDocumentHalResource resource = new StoredDocumentHalResource();
        resource.setCreatedBy("test");
        resource.setOriginalDocumentName("test.png");
        return new ResponseEntity<StoredDocumentHalResource>(resource, HttpStatus.OK);
    }

    private UUID getUuid() {
        return UUID.fromString(MATCHED_DOCUMENT_ID);
    }

    private CaseDocumentMetadata getCaseDocumentMetadata(String docId, List<Permission> permission) {
        Document document = Document.builder().permissions(permission).build();
        document.setId(docId);
        return CaseDocumentMetadata.builder().caseId("").documents(Optional.of(Arrays.asList(document))).build();
    }

    private ResponseEntity<ByteArrayResource> setDocumentBinaryContent(String responseType) {
        if (responseType.equals("OK")) {
            return new ResponseEntity<ByteArrayResource>(
                new ByteArrayResource("test document content".getBytes()),
                getHttpHeaders(),
                HttpStatus.OK
            );
        } else if (responseType.equals("forbidden")) {
            return new ResponseEntity<ByteArrayResource>(
                new ByteArrayResource("".getBytes()),
                getHttpHeaders(),
                HttpStatus.FORBIDDEN
            );
        }

        return new ResponseEntity<ByteArrayResource>(new ByteArrayResource("".getBytes()), HttpStatus.OK);
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("OriginalFileName", "OriginalFileName");
        headers.set("Content-Disposition", "Content-Disposition");
        headers.set("data-source", "data-source");
        headers.set("Content-Length", "25");
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
