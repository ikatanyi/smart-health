package io.smarthealth.clinical.laboratory.domain.enumeration;

public enum TestStatus { 
        Scheduled,
        AwaitingSpecimen,
        Accepted,
        Rejected,
        Completed,
        Cancelled,
        AwaitingReview 
}

 //AwaitingSpecimen, PendingResult, ResultsEntered