package software.amazon.fms.notificationchannel;

import org.mockito.ArgumentMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.DeleteNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.FmsException;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelRequest;
import software.amazon.awssdk.services.fms.model.GetNotificationChannelResponse;
import software.amazon.awssdk.services.fms.model.InvalidOperationException;
import software.amazon.awssdk.services.fms.model.ResourceNotFoundException;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.cloudformation.proxy.HandlerErrorCode;
import software.amazon.cloudformation.proxy.Logger;
import software.amazon.cloudformation.proxy.OperationStatus;
import software.amazon.cloudformation.proxy.ProgressEvent;
import software.amazon.cloudformation.proxy.ResourceHandlerRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeleteHandlerTest {

    @Mock
    private AmazonWebServicesClientProxy proxy;

    @Mock
    private Logger logger;

    private DeleteHandler handler;
    private String sampleSnsTopicArn;
    private String sampleSnsRoleName;
    private ResourceModel model;

    @BeforeEach
    public void setup() {
        proxy = mock(AmazonWebServicesClientProxy.class);
        logger = mock(Logger.class);
        handler = new DeleteHandler();
        sampleSnsTopicArn = "arn:aws:sns:us-east-1:012345678901:test-topic";
        sampleSnsRoleName = "arn:aws:iam::012345678901:role/aws-service-role/fms.amazonaws.com/AWSServiceRoleForFMS";

        // model the expected post-request resource state
        model = ResourceModel.builder().build();
    }

    @Test
    public void handleRequestSuccess() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // stub the response for the delete request
        final DeleteNotificationChannelResponse describeDeleteResponse =
                DeleteNotificationChannelResponse.builder().build();
        doReturn(describeDeleteResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the delete request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.SUCCESS);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isEqualTo(request.getDesiredResourceState());
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
    }

    @Test
    public void handleRequestReadResourceNotFound() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder().build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the delete request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequestDeleteResourceNotFoundException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock a ResourceNotFoundException from the FMS API
        doThrow(ResourceNotFoundException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the delete request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.NotFound);
    }

    @Test
    public void handleRequestDeleteInvalidOperationException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an InvalidOperationException from the FMS API
        doThrow(InvalidOperationException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the delete request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.InvalidRequest);
    }

    @Test
    public void handleRequestDeleteFmsException() {
        // stub the response for the read request
        final GetNotificationChannelResponse describeGetResponse = GetNotificationChannelResponse.builder()
                .snsTopicArn(sampleSnsTopicArn)
                .snsRoleName(sampleSnsRoleName)
                .build();
        doReturn(describeGetResponse)
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(GetNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // mock an FmsException from the FMS API
        doThrow(FmsException.builder().build())
                .when(proxy)
                .injectCredentialsAndInvokeV2(
                        ArgumentMatchers.isA(DeleteNotificationChannelRequest.class),
                        ArgumentMatchers.any()
                );

        // create the delete request and send it
        final ResourceHandlerRequest<ResourceModel> request = ResourceHandlerRequest.<ResourceModel>builder()
                .desiredResourceState(model)
                .build();
        final ProgressEvent<ResourceModel, CallbackContext> response =
                handler.handleRequest(proxy, request, null, logger);

        // assertions
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(OperationStatus.FAILED);
        assertThat(response.getCallbackContext()).isNull();
        assertThat(response.getCallbackDelaySeconds()).isEqualTo(0);
        assertThat(response.getResourceModel()).isNull();
        assertThat(response.getResourceModels()).isNull();
        assertThat(response.getMessage()).isNull();
        assertThat(response.getErrorCode()).isEqualTo(HandlerErrorCode.ServiceInternalError);
    }
}
