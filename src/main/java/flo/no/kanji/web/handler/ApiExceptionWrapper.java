package flo.no.kanji.web.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiExceptionWrapper {

	private String timestamp;
	private int status;
	private String error;
	private String message;
}
