package org.xcolab.client.proposals.exceptions;

public class ProposalNotFoundException extends RuntimeException {
    public ProposalNotFoundException(long proposalId) {
        this("Proposal with id " + proposalId + " does not exist");
    }

    public ProposalNotFoundException(String msg) {
        super(msg);
    }
}

