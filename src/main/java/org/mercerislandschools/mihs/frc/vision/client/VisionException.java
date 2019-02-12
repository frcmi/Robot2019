package org.mercerislandschools.mihs.frc.vision.client;

import org.mercerislandschools.mihs.frc.vision.client.model.ErrorInfo;

public class VisionException extends RuntimeException {
    public ErrorInfo errorInfo;

    public VisionException(ErrorInfo errorInfo) {
        super(errorInfo.message);
        this.errorInfo = errorInfo;
    }
}
