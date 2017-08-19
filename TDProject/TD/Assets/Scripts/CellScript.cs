using UnityEngine;
using UnityEngine.UI;
using System.Collections;

public class CellScript : MonoBehaviour {

    public Sprite txtr = null;
    public bool isRoad = false;
    public bool isBuildable = false;
    public bool is_target_for_menu = false;

    public GameObject tower = null;

	private Color cellSelectionColor;

	// Use this for initialization
    void Start()
    {
        if (txtr)
        {
            gameObject.GetComponent<SpriteRenderer>().sprite = txtr;
        }
		cellSelectionColor = GameObject.Find ("GENERATOR").GetComponent<GeneratorScript> ().cellSelectionColor;
    }
	
    void OnMouseEnter()
    {
        if(!is_target_for_menu)
		gameObject.GetComponent<SpriteRenderer> ().color = cellSelectionColor;
    }

    void OnMouseExit()
    {
        if(!is_target_for_menu)
        gameObject.GetComponent<SpriteRenderer>().color = Color.white;
    }

    void FixedUpdate()
    {
    }

    void OnMouseUp()
    {
        if (isBuildable)
        {
            workWithMenu("cellMenu+");
        }
        else if (tower != null)
        {
            workWithMenu("towerMenu");
        }
    }

    void workWithMenu(string menuName)
    {
        if (!ApplicationStatistics.menuIsCalled)
            ApplicationStatistics.menuIsCalled = true;
        else
            return;
        is_target_for_menu = true;
        gameObject.GetComponent<SpriteRenderer>().color = Color.yellow;
        var menu = GameObject.Find(menuName);

        if (menu.GetComponent<CellMenu>())
        {

            menu.GetComponent<CellMenu>().pos = gameObject.GetComponent<Transform>().position;
            menu.GetComponent<CellMenu>().master_cell = gameObject;
        }else if (menu.GetComponent<TowerMenu>())
        {

            menu.GetComponent<TowerMenu>().pos = gameObject.GetComponent<Transform>().position;
            menu.GetComponent<TowerMenu>().master_cell = gameObject;
            var towerSc = tower.GetComponent<TowerScript>();

            menu.transform.Find("towerInfo").GetComponent<Image>().sprite = towerSc.Look;
            var stats = menu.transform.Find("stats");
            stats.transform.Find("Damage").GetComponent<Text>().text =
                "Damage :  " + towerSc.Damage;
            stats.transform.Find("Range").GetComponent<Text>().text =
                "Range :   " + towerSc.Range;
            stats.transform.Find("Rate").GetComponent<Text>().text =
                "Rate :    " + towerSc.Rate;
            if (towerSc.Cost == -1)
                stats.transform.Find("Upgrade Cost").GetComponent<Text>().text =
                "Upgrade : Upgraded";
            else
                stats.transform.Find("Upgrade Cost").GetComponent<Text>().text =
                "Upgrade : " + towerSc.Cost + " coins";
        }

        Vector3 camera_pos = GameObject.Find("CAMERA").GetComponent<Transform>().position;
        camera_pos.z += 9;
        menu.GetComponent<Transform>().position = camera_pos;
    }
}
