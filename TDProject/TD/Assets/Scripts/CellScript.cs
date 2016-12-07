using UnityEngine;
using System.Collections;
using UnityEditor;

public class CellScript : MonoBehaviour {

    public Sprite txtr = null;
    public bool is_road = false;
    public bool is_buildable = false;


    public GameObject tower = null;

	// Use this for initialization
    void Start()
    {
        if (txtr)
        {
            gameObject.GetComponent<SpriteRenderer>().sprite = txtr;
        }
    }
	

    void OnMouseEnter()
    {
        gameObject.GetComponent<SpriteRenderer>().color = Color.blue;
    }

    void OnMouseExit()
    {
        gameObject.GetComponent<SpriteRenderer>().color = Color.white;
    }

    void OnMouseDown()
    {
       	//gameObject.GetComponent<SpriteRenderer>().color = Color.yellow;

    }
}