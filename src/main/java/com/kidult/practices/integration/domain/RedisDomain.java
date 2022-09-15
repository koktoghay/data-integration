package com.kidult.practices.integration.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Created by tommy on 2022/09/14.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class RedisDomain implements Serializable {

    private static final long serialVersionUID = 9157626609598927296L;

    private String company;

    private String employeeNo;

    private String employeeName;
}
