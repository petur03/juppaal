package uppaal;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Declaration extends UppaalElement{
	private static final Logger logger = LoggerFactory.getLogger(Declaration.class);

	List<String> declarations = new LinkedList<String>();
	public Declaration(Element declarationsElement){
		String[] decls = declarationsElement.getText().split("\n");
		for(String decl : decls)
			declarations.add(decl);
	}

	public Declaration(Declaration declarations) {
		this.declarations.add(declarations.toString().trim());
	}

	public void add(Declaration declarations){
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration(String declarations){
		this.declarations = new LinkedList<String>();
		this.declarations.add(declarations.toString().trim());
	}

	public Declaration() {
	}

	@Override
	public String getXMLElementName() {
		return "declaration";
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(String declaration : declarations){
			sb.append(declaration+"\n");
		}
		return sb.toString();
	}

	@Override
	public Element generateXMLElement() {
		Element result = super.generateXMLElement();
		result.addContent(toString());
		return result;
	}

	public List<String> getStrings() {
		return declarations;
	}

	public void add(String s) {
		declarations.add(s);
	}
	public void remove(String s) {
		declarations.remove(s);
	}


	public void replaceVar(double data, String name, boolean isConst) throws UppaalException {
		replaceVar(Double.toString(data), "double", name, isConst);
	}

	public void replaceVar(int data, String name, boolean isConst) throws UppaalException {
		replaceVar(Integer.toString(data), "int", name, isConst);
	}

	private void replaceVar(String dataS, String typeS, String name, boolean isConst) throws UppaalException {
		
		String pre = (isConst?"const ":"") + typeS + " " + name + " = ";
		boolean success = doReplace(dataS, pre, "[^;]*;");
		
		if (!success) {
			throw new UppaalException("replaceVar for \"" + name  + "\" failed!");
		}
	}

	public void replaceArray(double[] data, String name, boolean isConst) throws UppaalException {
		String dataS = Arrays.toString(data).replace("[", "{").replace("]", "}");
//		logger.debug(dataS);
		String pre = (isConst?"const ":"") + "double " + name + "\\[" + Integer.toString(data.length) + "\\] = ";
		boolean success = doReplace(dataS, pre, "\\{.*\\};");

		if (!success) {
			throw new UppaalException("replaceArray for \"" + name  + "\" failed! Array definition must be on a single line!");
		}
	}

	private boolean doReplace(String dataS, String pre, String arrayPart) {
		boolean success = false;
		String preReg = "\\s*" + pre.replace(" ", "\\s*");
//		logger.debug("pre:    " + pre);
//		logger.debug("preReg: " + preReg);
		String tmp;
		for (int i = 0; i < declarations.size(); i++) {
			String s = declarations.get(i);
			Pattern p = Pattern.compile(preReg + ".*");
			Matcher m = p.matcher(s);
//			logger.debug(s);
//			logger.debug(preReg + "\\{.*\\};");
			if (m.matches()) {
//				logger.debug(s);
//				logger.debug(preReg + "\\{.*\\};");
				tmp = s.replaceFirst(preReg + arrayPart, pre + dataS + ";");
//				logger.debug(tmp);
				declarations.set(i, tmp);
				success = true;
				break;
			}
		}
		return success;
	}
}
