using UnityEngine;
using System.Collections;

public class CellMenu : MonoBehaviour
{
    public GameObject master_cell;
    public Vector3 pos;
    private Vector3 V0 = new Vector3(-1000, -1000, -1);

    void Start()
    {
        exit();
    }

    public void build_tower(GameObject tower)
    {
        if (tower && GameObject.Find("COUNTER").GetComponent<CounterScript>().Cash(-tower.GetComponent<TowerScript>().cost[0]))
        {
            var tower_ = Instantiate(tower) as GameObject;
            tower_.GetComponent<Transform>().position = pos;

            master_cell.GetComponent<CellScript>().tower = tower_;
            master_cell.GetComponent<CellScript>().isBuildable = false;
            exit();
            return;
        }
    }

    public void exit()
    {
        gameObject.GetComponent<Transform>().position = V0;
        if (master_cell != null)
        {
            master_cell.GetComponent<CellScript>().is_target_for_menu = false;
            master_cell.GetComponent<SpriteRenderer>().color = Color.white;
        }
        master_cell = null;
        ApplicationStatistics.menuIsCalled = false;
    }
}