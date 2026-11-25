package com.hocuscoquus.hocuscoquus.service.export;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.openpdf.text.Document;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;

public class RecipeExportDownload extends RecipeExportAbstract{

	//file content
	private byte[] file;
	
	
	@Override
	public boolean export(Recipe recipe) {
			file = generateReport(recipe);
		
			return true;		
	}

    private byte[] generateReport(Recipe recipe) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            //Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
            
            
            // Title
            Paragraph titleHeader = getParagraph(recipe.getRecipeName(), titleFont, Element.ALIGN_CENTER, 20f);
            document.add(titleHeader);

            //Body Section
            //Description
            Paragraph descriptionHeader = getParagraph("Description", headerFont, Element.ALIGN_LEFT, 10f);
            document.add(descriptionHeader);
            
            Paragraph descriptionBody = getParagraph(recipe.getRecipeDescription(), bodyFont, Element.ALIGN_LEFT, 20f);
            document.add(descriptionBody);
            
            
            // Ingredients
            Paragraph ingredientHeader = getParagraph("Ingredients", headerFont, Element.ALIGN_LEFT, 10f);
            document.add(ingredientHeader);
            
            //Table of ingredients
            PdfPTable table = new PdfPTable(3);
            table.setSpacingBefore(10f);
            table.setWidthPercentage(100);

            String[] headers = new String[] {"Quantity", "Measurement", "Ingredient"};
            for (String col : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                cell.setPadding(6f);
                table.addCell(cell);
            }

            List<String[]> rows = getIngredientRows(recipe);
            for (String[] row : rows) {
                for (String value : row) {
                    PdfPCell cell = new PdfPCell(new Phrase(value));
                    cell.setPadding(5f);
                    table.addCell(cell);
                }
            }

            document.add(table);
            
            
            //Instructions
            Paragraph instructionHeader = getParagraph("Instructions", headerFont, Element.ALIGN_LEFT, 10f);
            document.add(instructionHeader);
            
            Paragraph instructionBody = getParagraph(recipe.getInstructions(), bodyFont, Element.ALIGN_LEFT, 20f);
            document.add(instructionBody);
            
            
            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
	
    
    //gets array of ingredients to generate table
    private List<String[]> getIngredientRows(Recipe recipe) {
    	List<String[]> ingredientRows = new ArrayList<>();
    	
    	for(RecipeIngredient i : recipe.getRecipeIngredients()) {
    		String[] temp = new String[] {String.valueOf(i.getQuantity()), i.getMeasurement(), i.getIngredient().getIngredientName() };
    		ingredientRows.add(temp);
    	}
    	
    	return ingredientRows;
    }

    //helper function to create paragraphs
	private Paragraph getParagraph(String content, Font font, int alignment, float spacing) {
		Paragraph p = new Paragraph(content, font);
        p.setAlignment(alignment);
        p.setSpacingAfter(spacing);
		return p;
	}
	
	public byte[] getFile() {
		return file;
	}
	
}
