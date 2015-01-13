/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todo.app.common.exception;

import java.util.ArrayList;
import java.util.List;

public class ErrorModel {

    private List<String> errorMessages;

    public ErrorModel() {
        this.errorMessages = new ArrayList<>();
    }
    
    public ErrorModel(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
    
    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }
}
