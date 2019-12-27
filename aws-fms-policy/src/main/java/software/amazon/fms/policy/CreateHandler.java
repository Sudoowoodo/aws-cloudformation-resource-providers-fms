package software.amazon.fms.policy;

import software.amazon.awssdk.services.fms.model.PutPolicyRequest;
import software.amazon.awssdk.services.fms.model.PutPolicyResponse;
import software.amazon.cloudformation.proxy.AmazonWebServicesClientProxy;
import software.amazon.fms.policy.helpers.CfnHelper;
import software.amazon.fms.policy.helpers.FmsHelper;

public class CreateHandler extends PolicyHandler<PutPolicyResponse> {

    @Override
    protected PutPolicyResponse makeRequest(
            final AmazonWebServicesClientProxy proxy,
            final ResourceModel desiredResourceState) {

        // make the create request
        final PutPolicyRequest putPolicyRequest = PutPolicyRequest.builder()
                .policy(FmsHelper.convertCFNResourceModelToFMSPolicy(desiredResourceState))
                .build();
        return proxy.injectCredentialsAndInvokeV2(putPolicyRequest, client::putPolicy);
    }

    @Override
    protected ResourceModel constructSuccessResourceModel(final PutPolicyResponse response) {

        // convert the create request response to a resource model
        return CfnHelper.convertFMSPolicyToCFNResourceModel(response.policy(), response.policyArn());
    }
}