package com.tden.encounterengine;

import lombok.AllArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stanislav Myachenkov on 07.12.2016.
 */

@AllArgsConstructor
public class EncounterEnginePage {

    Document doc;

    public int retrieveLevelNumber(){
        Elements foo = doc.select("input[name=LevelNumber]");
        Element num = foo.get(0);
        String numVal = num.attr("value");
        return Integer.parseInt(numVal);
    }

    public int retrieveLevelId(){
        Elements foo = doc.select("input[name=LevelId]");
        Element lid = foo.get(0);
        String lidVal = lid.attr("value");
        return Integer.parseInt(lidVal);
    }

    public String retrieveLevelName(){
        Element foo = doc.select("div.content").first();
        Elements lnameVal = foo.select("h2");
        return lnameVal.text();
    }

    public boolean isLastCodeCorrect(){
        Elements history = doc.select(".history");
        Element isCorrectClass = history.get(0).child(1);
        String foo = isCorrectClass.attributes().get("class");
        return foo.equals("correct");
    }

    // in retrieveSectors and retrieveBonuses we parse both list of all sectores
    // aswell as entered codes

    public List<LevelSector> retrieveSectors(){

        List<LevelSector> sectorList = new ArrayList<>();
        Element sectorsBlock = doc.select(".cols.w100per").first();

        if(sectorsBlock == null ){
            return sectorList;
        } else {
            Elements sectors = sectorsBlock.select("p");

            for (int i = 0; i < sectors.size(); i++) {

                LevelSector s;

                Element sector = sectors.get(i);
                String textSector = sector.text();
                String name = textSector.substring(0, textSector.indexOf(":"));

                // dont forget human-readable  i+1
                if(sector.childNodeSize() == 4){      // correct code - 4 nodes, empty - 2
                    // was to lazy and hope they dont have '(' or ')' in sector names
                   String codeValue = textSector.substring(textSector.indexOf(':') + 1, textSector.indexOf('(') -1);
                    s = new LevelSector(i + 1, name, codeValue);
                } else {
                    s = new LevelSector(i + 1, name);
                }

                sectorList.add(s);
            }
        }
        return sectorList;
    }

    public List<LevelSector> retrieveBonuses(){
        List<LevelSector> sectorList = new ArrayList<>();
        Elements sectors = doc.select(".cols.w100per");
        return sectorList;
    }
}
