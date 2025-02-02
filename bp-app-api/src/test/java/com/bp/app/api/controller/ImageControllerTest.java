package com.bp.app.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bp.app.api.exception.BpCustomException;
import com.bp.app.api.provider.JwtProvider;
import com.bp.app.api.provider.image.ImageProvider;
import com.bp.app.api.response.ErrorResponseMessage.ErrorCode;
import com.bp.app.api.response.image.PreSignedPutUrlResult;
import com.bp.domain.mysql.repository.MemberAdapterRepository;
import com.bp.s3.client.naver.NCPObjectStorageClient;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(controllers = ImageController.class)
@AutoConfigureMockMvc(addFilters = false)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    ImageProvider imageProvider;

    @MockBean
    private NCPObjectStorageClient ncpClient;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private MemberAdapterRepository memberAdapterRepository;

    @Test
    @DisplayName("PreSignedPutUrl 요청시 성공 후 PreSignedPutUrlResult 를 wrapping 한 ResponseMessage 객체 응답을 받는다.")
    void given_preSignedPutUrlRequest_when_succeed_then_getResponseMessageWrappingPreSignedPutUrlResult()
        throws Exception {
        // given
        when(imageProvider.providePreSignedPutUrl()).thenReturn(
            new PreSignedPutUrlResult("www.test.com", UUID.randomUUID().toString()));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/owner/v1/images/presigned-put-url")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // then
        resultActions
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.returnValue").exists())
            .andExpect(jsonPath("$.returnValue.preSignedUrl").exists())
            .andExpect(jsonPath("$.returnValue.fileId").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("PreSignedPutUrl 요청시 외부 서비스 처리 클라이언트에서 실패하면 IS002 에러 코드를 포함한 ErrorResponseMessage 객체 응답을 받는다.")
    void given_preSignedPutUrlRequest_when_failed_then_getErrorResponseMessageWrappingErrorCodeIS002()
        throws Exception {
        // given
        when(imageProvider.providePreSignedPutUrl()).thenThrow(
            new BpCustomException(ErrorCode.IS002));

        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get("/owner/v1/images/presigned-put-url")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED));

        // then
        resultActions
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.errorCode").value("IS002"))
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }

    @Test
    @DisplayName("PreSignedGetUrl 요청시 fileId 파라미터가 없으면 ErrorResponseMessage 객체 응답을 받는다.")
    void given_preSignedGetUrlRequestWithoutFileId_when_succeed_then_getErrResponseMessage()
        throws Exception {
        // when
        ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(
                    "/v1/images/presigned-get-url")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
        );

        // then
        resultActions
            .andExpect(status().is4xxClientError())
            .andExpect(jsonPath("$.errorCode").exists())
            .andExpect(jsonPath("$.errorMessage").exists())
            .andDo(print());
    }
}
