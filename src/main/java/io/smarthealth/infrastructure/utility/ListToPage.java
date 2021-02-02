package io.smarthealth.infrastructure.utility;


import io.smarthealth.clinical.record.data.WaitingRequestsData;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;


public class ListToPage {

    public static Page<WaitingRequestsData> map(List<WaitingRequestsData> content, int offset, int limit){

        PageRequest pageRequest = PageRequest.of(offset, limit);


        int total = content.size();
        int start = toIntExact(pageRequest.getOffset());
        int end = Math.min((start + pageRequest.getPageSize()), total);

        List<WaitingRequestsData> output = new ArrayList<>();

        if (start <= end) {
            output = content.subList(start, end);
        }

        return new PageImpl<>(
                output,
                pageRequest,
                total
        );


    }

}
