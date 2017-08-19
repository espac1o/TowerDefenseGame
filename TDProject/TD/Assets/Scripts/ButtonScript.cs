using UnityEngine;
using System.Collections;



public class ButtonScript : MonoBehaviour
{
    public bool isExit = false;
    public GameObject tower;
    
    void OnMouseEnter()
    {
        gameObject.GetComponent<SpriteRenderer>().material.color = Color.red;
    }

    void OnMouseUp()
    {
        var menu = GameObject.Find("CELLMENU");
        
        if (isExit)
        {
            exit();
            return;
        }

        if (tower && GameObject.Find("COUNTER").GetComponent<CounterScript>().Cash(-tower.GetComponent<TowerScript>().cost[0]))
        {
            
            var tower_ = Instantiate(tower) as GameObject;
            tower_.GetComponent<Transform>().position = menu.GetComponent<CellMenu>().pos;
            exit();
            return;
        }

        
    }

    void OnMouseExit()
    {
        gameObject.GetComponent<SpriteRenderer>().material.color = Color.white;
    }

    void exit()
    {
        var menu = GameObject.Find("CELLMENU");
        menu.GetComponent<Transform>().position = new Vector3(-1000, -1000, -1);
    }

}
